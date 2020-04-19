
import java.awt.Dimension;
import java.util.Random;
import javax.swing.*;

public class JBrainTetris extends JTetris{
	private JCheckBox brainActive;
	private JCheckBox animateFall;
	private JLabel textOk;
	private JSlider adversary;
	private Brain br;
	private Brain.Move nextPos;
	
	public JBrainTetris(int pixels) {
		super(pixels);
		br = new DefaultBrain();	
		nextPos = null;
	} 
	 
	public JComponent createControlPanel() {
		JComponent superPanel = super.createControlPanel();
		 
		JLabel textBrain = new JLabel("Brain");
		superPanel.add(textBrain);
		brainActive = new JCheckBox("Brain active");	
		superPanel.add(brainActive);
		animateFall = new JCheckBox("Animate Falling");
		animateFall.setSelected(true);
		superPanel.add(animateFall);
		textOk = new JLabel("OK");
		superPanel.add(textOk);
		
		JPanel littlePanel = new JPanel();
		JLabel TextAdvers = new JLabel("Adversary");
		littlePanel.add(TextAdvers);
		adversary = new JSlider(0, 100, 0);
		adversary.setPreferredSize(new Dimension(100,15));
		littlePanel.add(adversary);
		superPanel.add(littlePanel);
		return superPanel;
	}
	
	public void tick(int verb) { 
		if(brainActive.isSelected() == true) { 
			if(nextPos == null) {
				board.undo();
				nextPos = br.bestMove(board, currentPiece, board.getHeight(), nextPos);
			}
			if(nextPos != null) { 
				if(!nextPos.piece.equals(currentPiece)) {
					super.tick(ROTATE);
				}
				if(nextPos.x < currentX) {
					super.tick(LEFT);
				}else if(nextPos.x > currentX) {
					super.tick(RIGHT);
				}
				if(animateFall.isSelected() == false) {
					if(nextPos.x == currentX && nextPos.y < currentY ) {
						super.tick(DROP);
					}
				}
				nextPos = null;
			}
		}
		super.tick(verb); 
	}
	
	public Piece pickNextPiece() {
		Random rand = new Random(); 
		int randInt = rand.nextInt(100);
		int adv = adversary.getValue();
		int dif = adv - randInt;
		if(dif > 0) {
			textOk.setText("*OK*");
			Piece worstPiece = null; 
			double worstScore = 0.0;
			Brain.Move worstMove = new Brain.Move();
			Piece[] pieces = Piece.getPieces();
			for(int i = 0; i < pieces.length; i++) {
				worstMove = br.bestMove(board, pieces[i], board.getHeight(), worstMove);
				board.undo();
				if(worstScore < worstMove.score) {
					worstScore = worstMove.score;
					worstPiece = pieces[i];
				}
			} 
			return worstPiece; 
		} 
		textOk.setText("OK");
		return super.pickNextPiece();
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		JTetris tetris = new JBrainTetris(16);
		JFrame frame = JBrainTetris.createFrame(tetris);
		frame.setVisible(true);
	}
}