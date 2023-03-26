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

    public static int sumofCards(String cards) {
        char[] possibleLetters = {'k', 'q', 'j'};
        String[] cardList = cards.split("-");
        int sum = 0;
        for (String s : cardList) {
            for (char element : possibleLetters) {
                if (Character.toLowerCase(s.charAt(0)) == element) {
                    sum += 10;
                    break;
                }
                if (s.charAt(0) == 'a') {
                    sum += 11;
                    break;
                } if (s.charAt(0) == '?')
                    break;
                int number = Integer.parseInt(s.substring(0,1));
                sum += number;
                break;
            }

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
    public static boolean hitCheck(int playerPoints, int dealerPoints, String move, String dealerCards) {
        return ((playerPoints <= 21 && Objects.equals(move, "P Hit"))
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
    //VALID CHARACTER CHECK ON VAJA TEHA!!!!!!!!!!
    public static boolean moveCheck(String[] rowList) {
        String playerCards = rowList[5];
        String dealerCards = rowList[4];
        String[] playerCardsList = playerCards.split("-");
        String move = rowList[3];
        int playerSum = sumofCards(playerCards);
        int dealerSum = sumofCards(dealerCards);

        if ((((playerSum >= dealerSum && playerSum <= 21) || (dealerSum > 21 && playerSum <= 21)) && Objects.equals(move, "P Win")) && dealerSum >= 17)
            return true;
        if (((playerSum < dealerSum) || (playerSum > 21)) && Objects.equals(move, "P Lose"))
            return true;

        if (checkSameCards(dealerCards, playerCards))
            return true;

        if (hitCheck(playerSum, dealerSum, move, dealerCards))
            return true;

        if (Objects.equals(move, "P Joined") && countQuestionmarks(dealerCards) == 1)
            return true;
        if (Objects.equals(move, "P left") && Objects.equals(playerCards, "-") && Objects.equals(dealerCards, "-"))
            return true;

        if (Objects.equals(move, "D Show") && countQuestionmarks(dealerCards) == 1 && playerSum <= 21)
            return true;

        return Objects.equals(move, "D Redeal") && countQuestionmarks(dealerCards) == 1 && playerCardsList.length == 2;
    }

    public static ArrayList<String> wrongMoves(String fileName) throws Exception{
        ArrayList<WrongMove> wrongMoveList = new ArrayList<>();
        ArrayList<String> rows = new ArrayList<>();
        File file = new File(fileName);
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
                            rows.add(row);
                        }
                    }
                } else if (!moveCheck(rowList)) {
                    WrongMove wrong = new WrongMove(sessionTimeStamp, sessionID);
                    wrongMoveList.add(wrong);
                    rows.add(row);
                }
            }
        }
        System.out.println(wrongMoveList.toString());
        return rows;
    }


    public static void main(String[] args) throws Exception{
        ArrayList<String> textForFile = wrongMoves("game_data_2.txt");
        System.out.println(textForFile);
        FileOutputStream newFile = new FileOutputStream("Game_1_Answer.txt");
        for (String row : textForFile) {
            newFile.write(row.getBytes(StandardCharsets.UTF_8));
            newFile.write("\n".getBytes(StandardCharsets.UTF_8));
        }
        newFile.flush();
        newFile.close();
    }
}
