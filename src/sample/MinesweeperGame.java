package sample;

import sample.Color;
import sample.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField = 0;//количество мин на поле
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;//количество флагов
    private boolean isGameStopped;//переменная для остановки игры, когда нажал на мину
    private int countClosedTiles = SIDE * SIDE;//количество неоткрытых ячеек
    private int score = 0;//подсчет очков

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }
    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y, "");
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }
    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE*SIDE;
        countMinesOnField = 0;
        score = 0;
        setScore(score);
        createGame();
    }

    //переопределение обработчиков

    //обработка левой кнопки
    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped)
            restart();
        else
            openTile(x, y);
    }

    private void openTile(int x, int y){

        if (isGameStopped == false & gameField[y][x].isFlag == false & gameField[y][x].isOpen == false){

            gameField[y][x].isOpen = true;
            countClosedTiles--;

            //счетчик очков
            if (gameField[y][x].isMine == false){
                score += 5;
                setScore(score);
            }

            //если кол-во мин равно кол-ву неоткрытых ячеек, то игра выйграна
            if ((countClosedTiles == countMinesOnField) & (gameField[y][x].isMine == false))
                win();

            if (gameField[y][x].isMine == true){
                setCellValue(x, y, MINE);
            }
            else {
                setCellNumber(x,y, gameField[y][x].countMineNeighbors);
            }

            setCellColor(x, y, Color.GREEN);

            //если нажали на ячейку с миной, то стоп игра
            if (gameField[y][x].isMine == true && gameField[y][x].isOpen == true){
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }

            //используем рекурсию для открытия пустых ячеек
            if (gameField[y][x].countMineNeighbors == 0 & gameField[y][x].isMine == false){
                for (GameObject go : getNeighbors(gameField[y][x])){
                    if (go.isOpen == false){

                        openTile(go.x, go.y);
                    }
                }
                setCellValue(x, y, "");
            }

            if (gameField[y][x].countMineNeighbors != 0 & gameField[y][x].isMine == false){
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            }
        }
    }

    //обработка правой кнопки
    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void markTile(int x, int y){
         if (!isGameStopped) {
             if (gameField[y][x].isOpen == false && gameField[y][x].isFlag == false && countFlags > 0){
                 gameField[y][x].isFlag = true;
                 setCellValue(x, y, FLAG);
                 setCellColor(x,y, Color.BLUE);
                 countFlags--;
             }
             else if (gameField[y][x].isOpen == false && gameField[y][x].isFlag == true && countFlags >= 0){
                 gameField[y][x].isFlag = false;
                 setCellValue(x, y, "");
                 setCellColor(x,y, Color.ORANGE);
                 countFlags++;
             }
         }
    }


    private void countMineNeighbors(){
        List<GameObject> listCountNeighbors = new ArrayList<>();
        int count = 0;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if(gameField[y][x].isMine == true)
                    continue;
                listCountNeighbors = getNeighbors(gameField[y][x]);
                for (int i = 0; i < listCountNeighbors.size(); i++){
                    if (listCountNeighbors.get(i).isMine == true){
                        count++;
                    }
                }
                gameField[y][x].countMineNeighbors = count;
                count = 0;
            }
        }
    }

    //подсчет количества соседей
    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.BROWN, "Game over", Color.DARKGRAY, 100);
    }
    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.GOLD, "You are win", Color.AQUA, 100);
    }


}