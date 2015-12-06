package de.adrodoc55.minecraft.mpl.scribble;

import java.util.Arrays;

public class MultiScore implements Comparable<MultiScore> {

    private final int[] scores;

    public MultiScore(int[] scores) {
        super();
        if (scores == null) {
            throw new NullPointerException("Der Parameter 'scores' darf nicht null sein");
        }
        this.scores = scores;
    }

    @Override
    public int compareTo(MultiScore o) {
        for (int i = 0; i < scores.length; i++) {
            if (i >= o.scores.length) {
                return 1;
            }
            if (scores[i] > o.scores[i]) {
                return 1;
            }
            if (scores[i] < o.scores[i]) {
                return -1;
            }
        }
        if (scores.length < o.scores.length) {
            return -1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(scores);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MultiScore other = (MultiScore) obj;
        if (!Arrays.equals(scores, other.scores))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MultiScore [scores=" + Arrays.toString(scores) + "]";
    }

}
