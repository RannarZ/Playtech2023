import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class DataAnalyzer {
    /**
     * Checks if a WrongMove object with sessionID equal to given ID exists in the ArrayList
     * @param list list of WrongMove objects
     * @param id session ID
     * @return returns the index of WrongMove object with the given sessionID and -1 if it doesn't exist
     */
    public static int inList(ArrayList<WrongMove> list, int id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSessionID() == id)
                return i;
        }
        return -1;
    }

    /**
     * Method sumofCards returns the sum of card values in the given player's or dealer's hand.
     * This method assumes that all cards are valid.
     * @param cards String of the player's or dealer's hand
     * @return value of all the cards.
     */

    public static int sumofCards(String cards) {
        String[] cardList = cards.split("-");
        int sum = 0;
        for (String s : cardList) {
            if (s.length() == 3) {
                sum += 10;
                continue;
            }
            if (s.charAt(0) == 'a') {
                sum += 11;
                continue;
            } if(Character.isDigit(s.charAt(0))) {
                sum += Integer.parseInt(s.substring(0, 1));
                continue;
            }
            sum += 10;
        }
        return sum;
    }

    /**
     * Returns true or false whether there is same cards in the dealers hand and in the players hand.
     * @param dealerCards Dealers cards
     * @param playerCards Players cards
     * @return true if there is no same cards and false when there are same cards.
     */
    public static boolean checkSameCards(String dealerCards, String playerCards){
        String[] dCards = dealerCards.split("-");
        String[] pCards = playerCards.split("-");
        for (String cardD : dCards) {
            for (String cardP : pCards) {
                if (Objects.equals(cardD, cardP.toUpperCase()))
                    return false;
            }
        }
        return true;
    }

    /**
     * Method hitCheck checks whether a player or dealer can hit or stand.
     * @param playerPoints player's sum of card values
     * @param dealerPoints dealer's sum of card values
     * @param move which move is played
     * @param dealerCards String of dealer's cards for detecting hidden cards
     * @return true if it is possible to hit or stand in the given situation.
     */
    public static boolean hitCheck(int playerPoints, int dealerPoints, String move, String dealerCards) {
        return ((playerPoints < 20 && Objects.equals(move, "P Hit"))
                || (dealerPoints < 17 && Objects.equals(move, "D Hit") && countQuestionmarks(dealerCards) == 0)
                || (playerPoints <= 21 && Objects.equals(move, "P Stand"))
                || (dealerPoints >= 17 && Objects.equals(move, "D Stand") && countQuestionmarks(dealerCards) == 0));
    }

    /**
     * Looks whether there are more than one hidden cards in the dealers hand
     * @param dealer dealers hand
     * @return 1 if there is only one hidden card and 2 when there is more
     */
    private static int countQuestionmarks(String dealer) {
        int answer = 0;
        for (int i = 0; i < dealer.length(); i++) {
            if(Objects.equals(dealer.charAt(i), '?'))
                answer++;
            if (answer > 1)
                return answer;
        }
        return answer;
    }

    /**
     * Method arrayContain returns whether an element exists in the given array
     * Only char type
     * @param array array of chars
     * @param n a char element
     * @return true if the array contains element n
     */
    public static boolean arrayContain(char[] array, char n){
        for (char element : array) {
            if (Objects.equals(element, Character.toUpperCase(n))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method validCards checks whether all cards are correct in given hand
     * @param cards A string that contains the cards that a player or dealer posesses
     * @return true if all cards are valid.
     */
    public static boolean validCards(String cards) {
        String[] cardList = cards.split("-");
        char[] cardNames = {'S', 'D', 'H', 'C'};
        char[] cardPictures = {'K', 'Q', 'J', 'A'};
        int counter = cardList.length;
        for (String card : cardList) {
            if (Objects.equals(card, "?")) {
                counter--;
                continue;
            }
            if (card.length() == 3 && Objects.equals(card.substring(0, 2), "10")) {
                counter--;
                continue;
            }
                if (!arrayContain(cardNames, card.charAt(card.length() - 1))  || (!Character.isDigit(card.charAt(0)) && !arrayContain(cardPictures, card.charAt(0))))
                    return false;
                if (card.length() == 2 && (arrayContain(cardPictures, Character.toLowerCase(card.charAt(0)))
                        || (Integer.parseInt(card.substring(0, 1)) < 10) && Integer.parseInt(card.substring(0, 1)) > 1)) {
                    counter--;
                }

            }
        return counter == 0;
        }

    /**
     * Method moveCheck checks whether the given move is correct.
     * @param rowList A list of row paramaters (Session id, move ect)
     * @return returns true if the move made is correct
     */
    public static boolean moveCheck(String[] rowList) {
        String playerCards = rowList[5];
        String dealerCards = rowList[4];
        String[] playerCardsList = playerCards.split("-");
        String move = rowList[3];
        //Check whether there are any same cards or if all the cards are valid. Essential for other condition statements to work.
        if (!validCards(playerCards) || !validCards(dealerCards))
            return false;
        if (!checkSameCards(dealerCards, playerCards))
            return false;

        int playerSum = sumofCards(playerCards);
        int dealerSum = sumofCards(dealerCards);
        //Checking win and lose conditions
        if ((((playerSum >= dealerSum && playerSum <= 21) || (dealerSum > 21 && playerSum <= 21)) && Objects.equals(move, "P Win")) && dealerSum >= 17)
            return true;
        if (((playerSum < dealerSum) || (playerSum > 21)) && Objects.equals(move, "P Lose"))
            return false;

        if (hitCheck(playerSum, dealerSum, move, dealerCards))
            return true;
        if (Objects.equals(move, "P Joined") && countQuestionmarks(dealerCards) == 1)
            return true;
        if (Objects.equals(move, "P Left") && Objects.equals(playerCards, "-") && Objects.equals(dealerCards, "-"))
            return true;

        if (Objects.equals(move, "D Show") && countQuestionmarks(dealerCards) == 1 && playerSum <= 21)
            return true;

        return Objects.equals(move, "D Redeal") && countQuestionmarks(dealerCards) == 1 && playerCardsList.length == 2;
    }

    /**
     * Method wrongMoves checks whether there are any wrong moves in the game data
     * and returns an ArrayList containing rows to be written into the answer file.
     * @param fileName File name from where to read game data.
     * @return ArrayList of rows to write into the answer file.
     * @throws Exception
     */
    public static ArrayList<String> wrongMoves(String fileName) throws Exception{
        ArrayList<WrongMove> wrongMoveList = new ArrayList<>();
        ArrayList<String> rows = new ArrayList<>();
        File file = new File("resources/" + fileName);
        try(Scanner sc = new Scanner(file)){
            while (sc.hasNext()) {
                String row = sc.nextLine();
                String[] rowList = row.split(",");
                if (rowList.length != 6 || Objects.equals(rowList[0], ""))
                    continue;
                int sessionTimeStamp = Integer.parseInt(rowList[0].strip());
                int sessionID = Integer.parseInt(rowList[1].strip());
                int isInList = inList(wrongMoveList, sessionID);
                if (isInList != -1) {
                    if (wrongMoveList.get(isInList).getTimestamp() > sessionTimeStamp) {
                        if (!moveCheck(rowList)) {
                            wrongMoveList.get(isInList).setTimestamp(sessionTimeStamp);
                            rows.remove(wrongMoveList.get(isInList).getIndex());
                            wrongMoveList.get(isInList).setIndex(rows.size());
                            rows.add(row);
                        }
                    }
                } else if (!moveCheck(rowList)) {
                    WrongMove wrong = new WrongMove(sessionTimeStamp, sessionID, rows.size());
                    boolean didAdd = false;
                    if (wrongMoveList.size() != 0) {
                        //Loop for sorting the elements
                        for (int i = 0; i < wrongMoveList.size(); i++) {
                            if (wrong.getSessionID() < wrongMoveList.get(i).getSessionID()) {
                                wrongMoveList.add(i, wrong);
                                rows.add(i, row);
                                didAdd = true;
                                break;
                            }
                        }
                        if (!didAdd) {
                            wrongMoveList.add(wrong);
                            rows.add(row);
                        }
                    } else {
                        wrongMoveList.add(wrong);
                        rows.add(row);
                    }
                }
            }
        }
        return rows;
    }

    public static void main(String[] args) throws Exception{
        ArrayList<String> textForFile = wrongMoves("game_data.txt");
        FileOutputStream newFile = new FileOutputStream("analyzer_results.txt");
        for (String row : textForFile) {
            newFile.write(row.getBytes(StandardCharsets.UTF_8));
            newFile.write("\n".getBytes(StandardCharsets.UTF_8));
        }
        newFile.flush();
        newFile.close();
    }
}
