/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package saolei;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Wei Wang
*/

class Axis{
    int x , y ;
    Axis(int x , int y){
        this.x = x ;
        this.y = y ;
    }
}

//class Game{
//    int nums;
//    int[][] dimension;
//    Game(int nums , int[][] dimension){
//        this.nums = nums;
//        this.dimension = dimension;
//    }
//}
public class SaoLei {
    int h, v , nums;
    boolean[][] panel;
    HashSet<int[]> BombsAxis = new HashSet();
    int[] deltaX = { -1 , -1 , -1, 0 , 0 , 1 , 1 , 1};
    int[] deltaY = { -1 , 0 , 1, -1 , 1 , -1, 0, 1};
    SaoLei(int h , int v ,int nums){
        this.h = h;
        this.v = v;
        this.nums = nums;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SaoLei sa = new SaoLei(20,20,30);
        sa.run();
    }
    private void run(){
//---------------------------------------------------------------------------------init--------------------------------------------------------------------
        panel = initPanel();
        UI ui = new UI();
        ui.run();
        
        
//        for(boolean[] cur : panel){
//            System.out.println(Arrays.toString(cur));
//        }
//        Axis click = new Axis(8,9);
//        System.out.println(getSurroundingCt(click));
        
        
    }
    private boolean[][] initPanel(){
        boolean[][] panel = new boolean[h][v];
        HashSet<Integer> set = new HashSet();
        for(int i = 0 ; i < nums ; i ++){
            int ran = (int)Math.floor(Math.random() * h * v);
            while(set.contains(ran)){
                ran = (int)Math.floor(Math.random() * h * v);
            }
            set.add(ran);
            panel[ran/h][ran%v] = true;
            int[] arr = {ran/h , ran%v };
            BombsAxis.add(arr);
        }
        return panel;
    }
    
    private int getSurroundingCt(Axis point){
        int ct = 0;
        
        if(panel[point.x][point.y]){
            System.out.println("Bomb!!! you are Dead!");
            return -999;
        }
        for(int i = 0 ; i < 8 ; i++){
            int x = point.x + deltaX[i];
            int y = point.y + deltaY[i];
            if(inBound( x , y) && panel[x][y]){
                ct++;
            }
        }
        System.out.println("surrounding bomb ct is : " + ct );
        return ct;   
    }
    private boolean inBound(int x ,int y){
        if( x >= 0 && x < h && y >= 0 && y < v ){
            return true;
        }
        return false;
    }
    private class UI {
        int countMark = nums;
        JLabel log = new JLabel();
        JFrame frame = new JFrame("扫雷");
        JButton replay = new JButton("REPLAY");
        JButton[][] buttons = new JButton[v][h];
        HashMap<JButton , Axis> map = new HashMap();
        int length = 45;
        public void run(){ 
            for(int i = 0 ; i < v ; i ++){
               for(int j = 0 ; j < h ; j ++){
                   buttons[i][j] = new JButton("");
                   buttons[i][j].setSize(60, 60);
                   buttons[i][j].addActionListener(new ButtonAction());
                   buttons[i][j].addMouseListener(new MouseAct());
                   buttons[i][j].setBounds(i*length, j*length, length, length);
                   map.put(buttons[i][j], new Axis(i , j));
                   frame.add(buttons[i][j]);
               }
            }
            replay.addActionListener(new ButtonAction());
            replay.setBounds(450, 900, 90, 30);
            log.setBounds(50, 900, 400, 30);
            frame.add(replay);
            frame.add(log);
            frame.setSize(1000, 1000);
            frame.setLayout(null);
            
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);        
        }
        private class ButtonAction implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent ev){
                if(ev.getSource() == replay){
                    frame.dispose();
                    SaoLei sa = new SaoLei(20,20,nums);
                    sa.run();
                }
                else{
                    JButton clickedButton = (JButton) ev.getSource();
                    int ct = getSurroundingCt(map.get(clickedButton));
                    System.out.println("ct is " + ct);
                    Axis curAxis = map.get(clickedButton);
                    if(ct == 0){
                        HashSet<String> set = new HashSet();
                        Queue<Axis> que = new LinkedList();
                        que.offer(curAxis);
                        while(!que.isEmpty()){
                           
                           int size = que.size();
                           for(int i = 0 ; i < size ; i++){
                               Axis cur = que.poll();
                               for(int j = 0 ; j < deltaX.length ; j++){
                                   int x = cur.x + deltaX[j];
                                   int y = cur.y + deltaY[j];
                                   
                                   if(inBound(x , y)){
                                        if(set.contains(String.valueOf(x) + "-" + String.valueOf(y))){
                                            continue;
                                        }
                                        int count  = getSurroundingCt(new Axis(x , y));
                                        if(count > 0){
                                            buttons[x][y].setEnabled(false);
                                            buttons[x][y].setText(String.valueOf(count));
                        
                                            
                                        }
                                        else if(count == 0){
                                            que.offer(new Axis(x, y));
                                            buttons[x][y].setEnabled(false);
                                            buttons[x][y].setText("  ");
                                            
                                        }
                                        set.add(String.valueOf(x) + "-" + String.valueOf(y));
                                   }
                               }
                           }
                        }
                    }
                    clickedButton.setEnabled(false);

                    if(ct == -999){
                        clickedButton.setText("D");
                        for(int[] axis : BombsAxis){
                            buttons[axis[0]][axis[1]].setText("D");
                        }
                        disableButtons();
                        log.setText(" Bomb!!! YOU ARE DEAD!");
                        return;
                    }
                    //check remaining ct
                    int countTotal = 0;
                    for(JButton[] x : buttons){
                        for(JButton y : x){
                            if(y.isEnabled()){
                                countTotal++;
                            }
                        }
                    }
                    
                    if(countTotal == nums){
                        disableButtons();
                        for(int[] axis : BombsAxis){
                            buttons[axis[0]][axis[1]].setText("D");
                        }
                        log.setText("YOU WIN!");
                    }
                    System.out.println(countTotal);
                    if(ct != 0){
                        clickedButton.setText(String.valueOf(ct));
                    }
                    else{
                        clickedButton.setText("  ");
                    }
                    
                    
                }
            }
        
        }
        
        private void disableButtons(){
            for(JButton[] bt : buttons){
                for(JButton btt : bt){
                    btt.setEnabled(false);
                }
            }
        }
        private class MouseAct implements MouseListener {

        @Override
        public void mousePressed(MouseEvent e) {            
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON3){
                JButton bt  = (JButton)e.getSource();
                if(bt.isEnabled()){
                    if(bt.getText() == "M"){
                        countMark ++;
                        bt.setText("  ");
                        bt.setForeground(Color.black);
                        log.setText(String.valueOf(countMark));
                        return;
                    }
                    countMark --;
                    bt.setText("M");
                    bt.setForeground(Color.blue);
                    log.setText(String.valueOf(countMark));
                }
            }
        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

        public void mouseReleased(MouseEvent e) {

        }
    }
        
    }   
    
}
