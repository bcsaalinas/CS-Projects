#include <iostream>
#include <vector>
#include <string>
#include <ncurses.h>
#include <locale.h>
#include <unistd.h>
#include <random>
#include <cctype>
#include <chrono>
#include <thread>

using namespace std;

class Pregunta {
public:
    string texto;
    vector<string> opciones;
    int respuesta_correcta;
    int premio;
    string dificultad;

    Pregunta(string t, vector<string> ops, int correcta, int p, string diff)
        : texto(t), opciones(ops), respuesta_correcta(correcta), premio(p), dificultad(diff) {}
};

class Juego {
private:
    vector<Pregunta> preguntas;
    vector<int> premios = {100, 200, 300, 500, 1000, 2000, 4000, 8000, 16000, 32000, 64000, 125000, 250000, 500000, 1000000};
    int nivel_actual = 0;
    bool comodin_50_50 = true;
    int premio_actual = 0;
    const int TIEMPO_LIMITE = 30; // Tiempo límite en segundos

    void animacion_transicion() {
        clear();
        for (int i = 0; i < LINES; i++) {
            for (int j = 0; j < COLS; j++) {
                mvaddch(i, j, '*');
                refresh();
                usleep(1000);
            }
        }
        clear();
    }

    void dibujar_borde_decorativo(int color_pair = 6) {
        attron(COLOR_PAIR(color_pair) | A_BOLD);
        for (int i = 0; i < COLS; i++) {
            mvaddch(0, i, ACS_HLINE);
            mvaddch(LINES - 1, i, ACS_HLINE);
        }
        for (int i = 0; i < LINES; i++) {
            mvaddch(i, 0, ACS_VLINE);
            mvaddch(i, COLS - 1, ACS_VLINE);
        }
        mvaddch(0, 0, ACS_ULCORNER);
        mvaddch(0, COLS - 1, ACS_URCORNER);
        mvaddch(LINES - 1, 0, ACS_LLCORNER);
        mvaddch(LINES - 1, COLS - 1, ACS_LRCORNER);
        attroff(COLOR_PAIR(color_pair) | A_BOLD);
    }

    void dibujar_logo_grande() {
        clear();
        
        attron(COLOR_PAIR(3) | A_BOLD | A_UNDERLINE);
        
        mvprintw(LINES/2 - 11, COLS/2 - 35, " ____  ____   ___   ____    ___    **_**___   ___      ");
        mvprintw(LINES/2 - 10, COLS/2 - 35, "|    \\|    \\ /   \\ |    |  /  _]  /  ]      | /   \\     ");
        mvprintw(LINES/2 - 9, COLS/2 - 35, "|  o  )  D  )     ||__  | /  [_  /  /|      ||     |    ");
        mvprintw(LINES/2 - 8, COLS/2 - 35, "|   */|    /|  O  |*_|  ||    *]/  / |*|  |_||  O  |    ");
        mvprintw(LINES/2 - 7, COLS/2 - 35, "|  |  |    \\|     /  |  ||   [_/   \\_  |  |  |     |    ");
        mvprintw(LINES/2 - 6, COLS/2 - 35, "|  |  |  .  \\     \\  `  ||     \\     | |  |  |     |    ");
        mvprintw(LINES/2 - 5, COLS/2 - 35, "|__|  |__|\\_|\\___/ \\____||_____|\\_____| |__|   \\___/     ");
        
        mvprintw(LINES/2 - 1, COLS/2 - 15, "Quien quiere ser millonario");
        
        attroff(COLOR_PAIR(3) | A_BOLD | A_UNDERLINE);

        attron(COLOR_PAIR(2) | A_BOLD);
        for (int i = 0; i < COLS; i += 2) {
            mvaddch(LINES/2 + 1, i, '*');
            refresh();
            usleep(10000);
        }
        attroff(COLOR_PAIR(2) | A_BOLD);
    }

    void dibujar_pantalla_inicio() {
        dibujar_logo_grande();

        attron(COLOR_PAIR(4) | A_BOLD | A_BLINK);
        mvprintw(LINES/2 + 4, COLS/2 - 15, "Presiona ENTER para comenzar");
        attroff(COLOR_PAIR(4) | A_BOLD | A_BLINK);

        attron(COLOR_PAIR(1));
        mvprintw(LINES/2 + 6, COLS/2 - 20, "Usa A, B, C, D para responder");
        mvprintw(LINES/2 + 8, COLS/2 - 20, "Q: Comodin 50/50");
        attroff(COLOR_PAIR(1));

        refresh();
        getch();
    }

    void dibujar_panel_premios() {
        attron(COLOR_PAIR(5) | A_BOLD);
        mvprintw(2, COLS - 25, "PREMIOS");

        for (size_t i = 0; i < premios.size(); i++) {
            int color = (i < nivel_actual) ? 2 : 4;
            attron(COLOR_PAIR(color));

            if (i == nivel_actual) {
                mvprintw(4 + i, COLS - 25, ">>> $%d", premios[i]);
            } else {
                mvprintw(4 + i, COLS - 25, "$%d", premios[i]);
            }
            attroff(COLOR_PAIR(color));
        }
        attroff(COLOR_PAIR(5) | A_BOLD);
    }

    void dibujar_comodines() {
        attron(A_BOLD);
        mvprintw(LINES - 4, 5, "Q: 50/50 %s", comodin_50_50 ? "[DISPONIBLE]" : "[USADO]");
        attroff(A_BOLD);
    }

    void inicializar_preguntas() {
        vector<Pregunta> temp_preguntas = {
            Pregunta(
                "¿Cuál es el planeta más cercano al Sol?",
                {"A) Mercurio", "B) Venus", "C) Tierra", "D) Marte"},
                0, 100, "Fácil"
            ),
            Pregunta(
                "¿Cuántos continentes hay en el mundo?",
                {"A) 5", "B) 6", "C) 7", "D) 8"},
                2, 200, "Fácil"
            ),
            Pregunta(
                "¿Cuál es la capital de Francia?",
                {"A) Madrid", "B) París", "C) Roma", "D) Berlín"},
                1, 300, "Fácil"
            ),
            Pregunta(
                "¿En qué año llegó el hombre a la Luna?",
                {"A) 1965", "B) 1967", "C) 1969", "D) 1972"},
                2, 500, "Intermedio"
            ),
            Pregunta(
                "¿Cuál es el elemento químico más abundante en el universo?",
                {"A) Oxígeno", "B) Carbono", "C) Hidrógeno", "D) Helio"},
                2, 1000, "Intermedio"
            ),
            Pregunta(
                "¿Cuál es el principio de incertidumbre de Heisenberg?",
                {"A) La posición y la velocidad no se pueden conocer simultáneamente con precisión", "B) La energía no puede ser destruida", "C) Las partículas subatómicas pueden estar en múltiples lugares a la vez", "D) La gravedad es una curvatura en el espacio-tiempo"},
                0, 2000, "Difícil"
            ),
            // Nuevas preguntas añadidas
            Pregunta(
                "¿Cuál es el río más largo del mundo?",
                {"A) Amazonas", "B) Nilo", "C) Misisipi", "D) Yangtsé"},
                1, 4000, "Intermedio"
            ),
            Pregunta(
                "¿Quién pintó la Mona Lisa?",
                {"A) Vincent Van Gogh", "B) Pablo Picasso", "C) Leonardo da Vinci", "D) Miguel Ángel"},
                2, 8000, "Intermedio"
            ),
            Pregunta(
                "¿Cuál es el metal más abundante en la corteza terrestre?",
                {"A) Oro", "B) Plata", "C) Cobre", "D) Aluminio"},
                3, 16000, "Difícil"
            ),
            Pregunta(
                "¿En qué año comenzó la Primera Guerra Mundial?",
                {"A) 1905", "B) 1914", "C) 1918", "D) 1920"},
                1, 32000, "Difícil"
            ),
            Pregunta(
                "¿Cuál es la molécula de ADN más corta conocida?",
                {"A) Virus X174", "B) VIH", "C) Hepatitis B", "D) Bacteriófago Phi-X174"},
                3, 64000, "Experto"
            )
        };
        
        random_device rd;
        mt19937 g(rd());
        shuffle(temp_preguntas.begin(), temp_preguntas.end(), g);

        preguntas = temp_preguntas;
    }

    void usar_comodin_50_50() {
        if (!comodin_50_50) return;

        vector<int> opciones_incorrectas;
        for (int i = 0; i < 4; i++) {
            if (i != preguntas[nivel_actual].respuesta_correcta) {
                opciones_incorrectas.push_back(i);
            }
        }

        random_device rd;
        mt19937 g(rd());
        shuffle(opciones_incorrectas.begin(), opciones_incorrectas.end(), g);

        for (int i = 0; i < 2; i++) {
            preguntas[nivel_actual].opciones[opciones_incorrectas[i]] = " ";
        }

        comodin_50_50 = false;
    }

    bool mostrar_pregunta_con_temporizador() {
        int tiempo_restante = TIEMPO_LIMITE;
        
        while (tiempo_restante > 0) {
            clear();
            dibujar_borde_decorativo();
            dibujar_panel_premios();
            dibujar_comodines();

            // Mostrar la pregunta
            mvprintw(LINES/2, COLS/2 - preguntas[nivel_actual].texto.length()/2, preguntas[nivel_actual].texto.c_str());

            // Mostrar las opciones de respuesta
            for (int i = 0; i < 4; i++) {
                mvprintw(LINES/2 + 2 + i, COLS/2 - 10, preguntas[nivel_actual].opciones[i].c_str());
            }

            // Mostrar temporizador
            attron(COLOR_PAIR(4) | A_BOLD);
            mvprintw(LINES - 2, COLS/2 - 10, "Tiempo restante: %d segundos", tiempo_restante);
            attroff(COLOR_PAIR(4) | A_BOLD);

            refresh();

            // Configurar timeout para getch
            timeout(1000);  // 1 segundo
            char respuesta = tolower(getch());

            if (respuesta == 'q') {
                usar_comodin_50_50();
                continue;
            } else if (respuesta != ERR) {
                char opciones[] = {'a', 'b', 'c', 'd'};
                if (respuesta == opciones[preguntas[nivel_actual].respuesta_correcta]) {
                    return true;
                } else {
                    return false;
                }
            }

            tiempo_restante--;
        }

        return false;  // Tiempo agotado
    }

public:
    Juego() {
        inicializar_preguntas();
        initscr();
        cbreak();
        noecho();
        start_color();
        init_pair(1, COLOR_WHITE, COLOR_BLACK);
        init_pair(2, COLOR_GREEN, COLOR_BLACK);
        init_pair(3, COLOR_CYAN, COLOR_BLACK);
        init_pair(4, COLOR_YELLOW, COLOR_BLACK);
        init_pair(5, COLOR_RED, COLOR_BLACK);
        init_pair(6, COLOR_MAGENTA, COLOR_BLACK);
        keypad(stdscr, TRUE);
    }

    void jugar() {
        dibujar_pantalla_inicio();
        while (nivel_actual < preguntas.size()) {
            animacion_transicion();
            
            if (!mostrar_pregunta_con_temporizador()) {
                break;  // Respuesta incorrecta o tiempo agotado
            }

            premio_actual = premios[nivel_actual];
            nivel_actual++;
        }

        clear();
        dibujar_borde_decorativo();
        mvprintw(LINES/2, COLS/2 - 15, "GAME OVER! Premio ganado: $%d", premio_actual);
        mvprintw(LINES/2 + 2, COLS/2 - 15, "Presiona cualquier tecla para salir");
        refresh();
        getch();
    }
};

int main() {
    Juego juego;
    juego.jugar();
    endwin();
    return 0;
}

//to run game use command: g++ Final2.cpp -o juego -lncurses && ./juego