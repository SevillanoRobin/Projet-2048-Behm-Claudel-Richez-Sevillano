/*
 * Copyright (c) 09/12/2019
 *
 * Auteurs :
 *      - Behm Guillaume
 *      - Claudel Adrien
 *      - Richez Guillaume
 *      - Sevillano Robin
 */
package model;

import java.io.*;
import java.util.Calendar;

/**
 * Ensemble des 3 grilles
 *
 * @author Robin
 */
public class Grids extends Movable implements Serializable {

    private Grid[] grids;

    /**
     * Constructeur de la matrice 3D
     */
    public Grids() {
        grids = new Grid[3];

        for (int index = 0; index < 3; index++) {
            grids[index] = new Grid();
        }
    }

    /**
     * Creé un nouvelle ensemble de 3 grilles tout en conservant les propriétés de la grille passée en paramètre
     *
     * @param grid
     */
    public Grids(Grid[] grid) {
        this.grids = new Grid[SIDE];
        for (int index = 0; index < SIDE; index++) {
            this.grids[index] = new Grid(grid[index].copy());
        }
    }

    /**
     * Meilleur score
     *
     * @return
     */
    public int best() {
        int score = 0;

        for (Grid g : grids) {
            if (g.best() > score) {
                score = g.best();
            }
        }

        return score;
    }

    /**
     * Score totale
     *
     * @return
     */
    public int scoreTotalGrille() {
        return scoreTotalGrille_Core(1);
    }

    /**
     * Renvoie un score supérieur au score total car le poids des tuiles est différent selon leur valeur
     *
     * @return
     */
    public int scoreTotalGrilleMajore() {
        return scoreTotalGrille_Core(1.5);
    }

    private int scoreTotalGrille_Core(double _multiplicator) {
        int score = 0;
        for (Grid g : this.grids) {
            for (Tile t : g.getGrid()) {
                if (t != null) {
                    score += t.getValue() * _multiplicator;
                }
            }
        }
        return score;
    }

    /**
     * Verifie si les 3 grilles sont bloquées
     * <p>
     * Vérifie au travers d'une grille réorganisée (temp) et des grilles courantes.
     *
     * @return boolean
     */
    public boolean stillPlayeable() {
        int nbGrids = this.grids.length;
        int nbBoolean = 0;

        Grid[] temp = reorganization(this.grids);

        for (int i = 0; i < nbGrids; i++) {
            if (temp[i].lose()) {
                nbBoolean++;
            }

            if (this.grids[i].lose()) {
                nbBoolean++;
            }
        }

        return nbBoolean != nbGrids * 2;
    }

    /**
     * Verifie si la valeur de la tuile la plus élevé est égale ou supérieur à la valeur but
     *
     * @return
     */
    public boolean victory() {
        return best() >= GOAL;
    }

    /**
     * Gere les depacements des grilles
     *
     * @param simulation
     * @param _d
     */
    public void move(boolean simulation, int _d) {
        boolean[] verif = { false, false, false };

        if (_d == FRONT) {
            moveBackOrForth(verif, DOWN);

        } else if (_d == BACK) {
            moveBackOrForth(verif, UP);

        } else {
            for (int index = 0; index < SIDE; index++) {
                verif[index] = grids[index].move(_d);
            }
        }

        for (boolean b : verif) {
            if (b) {
                for ( int i = 0; i < 100; i++) {
                    int random = (int) (Math.random() * this.grids.length);
                    Grid randomGrid = this.grids[random];

                    for (int j = 0; j < randomGrid.getGrid().length; j++) {
                        if (randomGrid.getGrid()[j] == null && !simulation) {
                            randomGrid.newTile();
                            return;
                        }
                    }
                }
            }
        }
    }

    private void moveBackOrForth(boolean[] _verif, int _direction) {
        Grids tamp = new Grids(reorganization(this.grids));
        for (int index = 0; index < SIDE; index++) {
            _verif[index] = tamp.getGrids()[index].move(_direction);
        }

        tamp.setGrids(this.reorganizationInverse(tamp.getGrids())); //on remet la matrice dans le bon sens
        if (this.equals(tamp.grids)) {
            _verif[0] = false;
        } else {
            this.grids = tamp.grids;
            _verif[0] = true;
        }
    }

    /**
     * Getter
     *
     * @return
     */
    public Grid[] getGrids() {
        return this.grids;
    }


    /**
     * Setter
     *
     * @param _gs
     */
    private void setGrids(Grid[] _gs) {
        this.grids = _gs;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Grid g : grids) {
            s.append(g.toString());
        }
        return s.toString();
    }

    /**
     * Pivote la matrice de jeu
     *
     * @param _gs
     *
     * @return
     */
    public Grid[] reorganization(Grid[] _gs) {
        Tile[] result1 = new Tile[9];
        Tile[] result2 = new Tile[9];
        Tile[] result3 = new Tile[9];

        int index2 = 2, index3 = 0;
        for (int index1 = 0; index1 < SIZE; index1++) {
            if (_gs[index2].getGrid()[index3] != null) {
                result1[index1] = _gs[index2].getGrid()[index3];
            }
            if (_gs[index2].getGrid()[index3 + SIDE] != null) {
                result2[index1] = _gs[index2].getGrid()[index3 + SIDE];
            }
            if (_gs[index2].getGrid()[index3 + (SIDE * 2)] != null) {
                result3[index1] = _gs[index2].getGrid()[index3 + (SIDE * 2)];
            }

            index3++;
            if (index1 == 2 || index1 == 5) {
                index2--;
                index3 = 0;
            }
        }

        return new Grid[] { new Grid(result1), new Grid(result2), new Grid(result3) };
    }

    /**
     * Réorganise la grille dans le sens inverse à reorganization(Grids[] _gs)
     *
     * @param _gs
     *
     * @return
     */
    public Grid[] reorganizationInverse(Grid[] _gs) {
        Tile[] result1 = new Tile[9];
        Tile[] result2 = new Tile[9];
        Tile[] result3 = new Tile[9];

        for (int grille = 1; grille < 4; grille++) {
            for (int ligne = 0; ligne < SIDE; ligne++) {
                for (int colonne = 0; colonne < SIDE; colonne++) {
                    if (grille == 1) {
                        if (ligne == 0) {
                            result3[colonne] = _gs[grille - 1].getGrid()[colonne];
                        } else if (ligne == 1) {
                            result2[colonne] = _gs[grille - 1].getGrid()[colonne + SIDE];
                        } else {
                            result1[colonne] = _gs[grille - 1].getGrid()[colonne + SIDE * ligne];
                        }
                    } else if (grille == 2) {
                        if (ligne == 0) {
                            result3[colonne + SIDE] = _gs[grille - 1].getGrid()[colonne];
                        } else if (ligne == 1) {
                            result2[colonne + SIDE] = _gs[grille - 1].getGrid()[SIDE + colonne];
                        } else {
                            result1[SIDE + colonne] = _gs[grille - 1].getGrid()[colonne + SIDE * ligne];
                        }
                    } else {
                        if (ligne == 0) {
                            result3[colonne + SIDE * 2] = _gs[grille - 1].getGrid()[colonne];
                        } else if (ligne == 1) {
                            result2[colonne + SIDE * 2] = _gs[grille - 1].getGrid()[colonne + SIDE];
                        } else {
                            result1[ligne * SIDE + colonne] = _gs[grille - 1].getGrid()[colonne + SIDE * ligne];
                        }
                    }

                }
            }
        }
        return new Grid[] { new Grid(result1), new Grid(result2), new Grid(result3) };
    }

    /**
     * Permet l'affichage des grilles en console
     */
    public void affichage() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < SIDE; i++) {
            if (i == 0) {
                s.append("|--------------------|");
            } else {
                s.append("  |--------------------|");
            }
        }

        for (int x = 0; x < SIDE; x++) {
            s.append("\n|");
            for (int index = 0; index < SIDE; index++) {
                for (int y = 0; y < SIDE; y++) {
                    if (grids[index].getGrid()[x * SIDE + y] == null) {
                        s.append("      ");
                    } else {
                        int givenGridValue = grids[index].getGrid()[x * SIDE + y].getValue();
                        if (givenGridValue < 9) {
                            s.append("  ").append(givenGridValue).append("   ");
                        } else if (givenGridValue < 99) {
                            s.append("  ").append(givenGridValue).append("  ");
                        } else if (givenGridValue < 999) {
                            s.append("  ").append(givenGridValue).append(" ");
                        } else if (givenGridValue < 9999) {
                            s.append(" ").append(givenGridValue).append(" ");
                        }
                    }
                    s.append("|");
                }
                if (index + 1 < SIDE) {
                    s.append("  |");
                }
            }
        }
        s.append("\n");
        for (int i = 0; i < SIDE; i++) {
            if (i == 0) {
                s.append("|--------------------|");
            } else {
                s.append("  |--------------------|");
            }
        }
        System.out.println(s);
    }

    /**
     * Permet de vérifier si deux ensembles de grilles sont égales
     *
     * @param g
     *
     * @return
     */
    public boolean equals(Grid[] g) {
        for (int grille = 0; grille < SIDE; grille++) {
            for (int t = 0; t < SIZE; t++) {
                Tile tile1 = this.grids[grille].getGrid()[t];
                Tile tile2 = g[grille].getGrid()[t];

                if (tile1 != null && tile2 != null) {
                    if (!(tile1.compareValeur(tile2))) {
                        return false;
                    }
                } else if (tile1 == null && tile2 != null) {
                    return false;
                } else if (tile1 != null) {
                    return false;
                }

            }
        }
        return true;
    }

    public void save() {
        String strDate = Calendar.getInstance().getTime().toString();

        try {
            FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "2048_" + strDate + ".xt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(this.grids);
            oos.close();
            System.out.println("Sauvegarder : " + System.getProperty("user.dir") + "2048_" + strDate + ".xt");
        } catch (IOException _e) {
            _e.printStackTrace();
        }

    }


    public void load(String _path) {
        // Deserialization
        try {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(_path);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            this.grids = (Grid[]) in.readObject();

            in.close();
            file.close();

            System.out.println("Chargement complet");


        } catch (IOException ex) {
            System.out.println("IOException is caught");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");
        }
    }
}
