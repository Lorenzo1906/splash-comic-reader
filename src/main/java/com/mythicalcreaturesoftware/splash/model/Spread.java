package com.mythicalcreaturesoftware.splash.model;

import java.awt.*;

/**
 * Logical representation of an spread. A spread is simply a set of pages (usually two) viewed together.
 * The parts of a spread are recto, and verso. Recto is the righthand page in a book; recto pages always have odd page numbering (e.g., pages 1, 3, 5).
 * Verso is the lefthand page in a book; named for the reverse or back side of the page; verso pages always have even page numbering (e.g., pages 2, 4, 6)
 */
public class Spread {

    private String recto;
    private Integer rectoPageNumber;
    private Dimension rectoSize;
    private String verso;
    private Integer versoPageNumber;
    private Dimension versoSize;

    /**
     * Returns the current recto of the spread
     * @return a string with the current path of the recto, can be empty
     */
    public String getRecto() {
        return recto;
    }

    /**
     * Sets the current recto of the spread
     * @param recto
     */
    public void setRecto(String recto) {
        this.recto = recto;
    }

    /**
     * Returns the current verso of the spread
     * @return a string with the current path of the verso, can be empty
     */
    public String getVerso() {
        return verso;
    }

    /**
     * Sets the current verso of the spread
     * @param verso
     */
    public void setVerso(String verso) {
        this.verso = verso;
    }

    /**
     * Returns the current page number for the recto, can be null
     * @return
     */
    public Integer getRectoPageNumber() {
        return rectoPageNumber;
    }

    /**
     * Sets the current page number for the recto
     * @param rectoPageNumber
     */
    public void setRectoPageNumber(Integer rectoPageNumber) {
        this.rectoPageNumber = rectoPageNumber;
    }

    /**
     * Returns the current page number for the verso, can be null
     * @return
     */
    public Integer getVersoPageNumber() {
        return versoPageNumber;
    }

    /**
     * Sets the current page number for the verso
     * @param versoPageNumber
     */
    public void setVersoPageNumber(Integer versoPageNumber) {
        this.versoPageNumber = versoPageNumber;
    }

    /**
     * Returns the current size of the recto
     * @return
     */
    public Dimension getRectoSize() {
        return rectoSize;
    }

    /**
     * Sets the current size of the recto
     * @param rectoSize
     */
    public void setRectoSize(Dimension rectoSize) {
        this.rectoSize = rectoSize;
    }

    /***
     * Returns the current size of the verso
     * @return
     */
    public Dimension getVersoSize() {
        return versoSize;
    }

    /**
     * Returns the current size of the verso
     * @param versoSize
     */
    public void setVersoSize(Dimension versoSize) {
        this.versoSize = versoSize;
    }
}
