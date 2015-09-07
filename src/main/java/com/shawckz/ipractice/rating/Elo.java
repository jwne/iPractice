package com.shawckz.ipractice.rating;


import java.util.HashMap;
import java.util.StringTokenizer;

public class Elo {

    public final static int SUPPORTED_PLAYERS = 2;

    // Score constants
    public final static double WIN = 1.0;
    public final static double DRAW = 0.5;
    public final static double LOSS = 0.0;

    // Attributes
    public KFactor [] kFactors = {};

    // List of singletons are stored in this HashMap
    private static HashMap ratingSystems = null;

    /**
     * Constructor to the JOGRE ELO rating system.
     *
     */
    public Elo() {


        // Read k factor in from server properties
        String kFactorStr = "0-10000=34";

        if (kFactorStr != null) {
            // Split each of the kFactor ranges up (kfactor1,factor2, etc)
            StringTokenizer st1 = new StringTokenizer (kFactorStr, ",");
            kFactors = new KFactor [st1.countTokens()];

            int index = 0;
            while (st1.hasMoreTokens()) {
                String kfr = st1.nextToken();

                // Split the range from the value (range=value)
                StringTokenizer st2 = new StringTokenizer (kfr, "=");
                String range = st2.nextToken();

                // Retrieve value
                double value = Double.parseDouble (st2.nextToken());

                // Retrieve start end index from the range
                st2 = new StringTokenizer (range, "-");
                int startIndex = Integer.parseInt(st2.nextToken());
                int endIndex   = Integer.parseInt(st2.nextToken());

                // Add kFactor to range
                kFactors [index++] = new KFactor (startIndex, endIndex, value);
            }
        }
    }

    /**
     * Return instance of an ELO rating system.
     *
     * @return       ELO rating system for specified game.
     */
    public static synchronized Elo getInstance () {
        if (ratingSystems == null)
            ratingSystems = new HashMap ();

        // Retrieve rating system
        Object ratingSystem = ratingSystems.get ("game");

        // If null then create new one and add to hash keying off the game
        if (ratingSystem == null) {
            ratingSystem = new Elo ();
            ratingSystems.put ("game", ratingSystem);

            return (Elo)ratingSystem;
        }
        else
            return (Elo)ratingSystem;
    }

    /**
     * Get new rating.
     *
     * @param rating
     *            Rating of either the current player or the average of the
     *            current team.
     * @param opponentRating
     *            Rating of either the opponent player or the average of the
     *            opponent team or teams.
     * @param score
     *            Score: 0=Loss 0.5=Draw 1.0=Win
     * @return the new rating
     */
    public int getNewRating(int rating, int opponentRating, double score) {
        double kFactor       = getKFactor(rating);
        double expectedScore = getExpectedScore(rating, opponentRating);
        int    newRating     = calculateNewRating(rating, score, expectedScore, kFactor);

        return newRating;
    }

    /**
     * Calculate the new rating based on the ELO standard formula.
     * newRating = oldRating + constant * (score - expectedScore)
     *
     * @param oldRating     Old Rating
     * @param score                 Score
     * @param expectedScore Expected Score
     * @return                              the new rating of the player
     */
    private int calculateNewRating(int oldRating, double score, double expectedScore, double kFactor) {
        return oldRating + (int) (kFactor * (score - expectedScore));
    }

    /**
     * This is the standard chess constant.  This constant can differ
     * based on different games.  The higher the constant the faster
     * the rating will grow.  That is why for this standard chess method,
     * the constant is higher for weaker players and lower for stronger
     * players.
     *
     * @param rating                Rating
     * @return                              Constant
     */
    private double getKFactor (int rating) {
        // Return the correct k factor.
        for (int i = 0; i < kFactors.length; i++)
            if (rating >= kFactors[i].getStartIndex() &&
                    rating <= kFactors[i].getEndIndex())
            {
                return kFactors[i].value;
            }
        return 34;
    }

    /**
     * Get expected score based on two players.  If more than two players
     * are competing, then opponentRating will be the average of all other
     * opponent's ratings.  If there is two teams against each other, rating
     * and opponentRating will be the average of those players.
     *
     * @param rating                        Rating
     * @param opponentRating        Opponent(s) rating
     * @return                                      the expected score
     */
    private double getExpectedScore (int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, ((double) (opponentRating - rating) / 400.0)));
    }

    /**
     * Small inner class data structure to describe a KFactor range.
     */
    public class KFactor {

        private int startIndex, endIndex;
        private double value;

        public KFactor (int startIndex, int endIndex, double value) {
            this.startIndex = startIndex;
            this.endIndex   = endIndex;
            this.value      = value;
        }
        public int getStartIndex () { return startIndex; }
        public int getEndIndex ()   { return endIndex; }
        public double getValue ()      { return value; }

        public String toString () {
            return "kfactor: " + startIndex + " " + endIndex + " " + value;
        }
    }
}