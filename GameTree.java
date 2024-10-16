package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

// A model for the game of 20 questions. This type can be used to
// build a console based game of 20 questions or a GUI based game.
//
// @author Rick Mercer and Morya Odak
//
public class GameTree {

	// BinaryTreeNode inner class used to create new nodes in the GameTree.
	private class TreeNode {

		// Instance variables
		private String data;
		private TreeNode left;
		private TreeNode right;

		TreeNode(String theData) {
			data = theData;
			left = null;
			right = null;
		}

		// This 2nd constructor is needed in a few methods, like privste build()
		TreeNode(String theData, TreeNode leftLink, TreeNode rightLink) {
			data = theData;
			left = leftLink;
			right = rightLink;
		}
	}

	// Instance variables
	private TreeNode root;
	private TreeNode currentNode;
	private Scanner scanner;

	private String fileName;

	// Constructor needed to create the game. It should open the input
	// file and call the recursive method build(). The String parameter
	// name is the name of the file from which we need to read the game
	// questions and answers from.
	//
	public GameTree(String name) {
		fileName = name;
		try {
			scanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: '" + fileName + "'");
		}
		root = build();
		currentNode = root;
		scanner.close();
	}

	String accumulate = "";

	private String addUp(TreeNode t, int depth) {
		if (t != null) {
			addUp(t.right, depth + 1);
			for (int i = 1; i <= depth; i++) {
				accumulate += "	";
			}
			accumulate += t.data + "\n";
			addUp(t.left, depth + 1);

		}
		return accumulate;
	}

	// Build a GameTree in preorder fashion and return the root of the tree
	private TreeNode build() {
		// One base case
		if (!scanner.hasNext())
			return null;

		String token = scanner.nextLine();

		// Another base case
		if (token.charAt(token.length() - 1) != '?') {
			return new TreeNode(token);
		}

		else {
			// Recursive case. As with trees, we almost always go left and right.
			TreeNode leftSubtree = build();
			TreeNode rightSubtree = build();
			return new TreeNode(token, leftSubtree, rightSubtree);
		}
	}

	// Method used to print out a text version of the game file
	// in a level order fashion
	@Override
	public String toString() {
		return addUp(currentNode, 0);
	}

	// Add a new question and answer to the currentNode. If the currentNode
	// is referencing the answer "parrot", theGame.add("Does it swim?", "duck");
	// should change the GameTree on the left to the GameTree on the right:
	//
	// Feathers? Feathers?
	// / \ / \
	// parrot horse Does it swim? horse
	// / \
	// duck parrot
	//
	// @param newQuestion: The question to add where the old answer was.
	// @param newAnswer: The new yes answer to the new question.
	//
	// Precondition: newQuestion.endsWith("?")
	//
	public void add(String newQuestion, String newAnswer) {
		if (!getCurrent().endsWith("?")) {
			String rootData = currentNode.data;
			currentNode.data = newQuestion;
			currentNode.left = new TreeNode(newAnswer);
			currentNode.right = new TreeNode(rootData);
		} else {
			TreeNode temp = currentNode.left;
			TreeNode newQNode = new TreeNode(newQuestion);
			currentNode.left = newQNode;
			TreeNode yesNode = new TreeNode(newAnswer);
			currentNode.left.left = yesNode;
			currentNode.left.right = temp;
		}
	}

	// Return true if getCurrent() is an answer rather than a question. Return false
	// if the current node is an internal node rather than a leaf that is an answer.
	public boolean foundAnswer() {
		if (getCurrent().charAt(getCurrent().length() - 1) == '?') {
			return false;
		}
		return true;
	}

	// Return the data for the current node,
	// which could be a question or an answer.
	public String getCurrent() {
		return currentNode.data;
	}

	// Ask the game to update the current node in the tree by
	// going left for Choice.yes or right for Choice.no
	// Example code:
	// theGame.playerSelected(Choice.Yes);
	//
	public void playerSelected(Choice yesOrNo) {
		if (yesOrNo == Choice.YES) {
			currentNode = currentNode.left;
		} else {
			currentNode = currentNode.right;
		}

	}

	// Begin a game at the root of the tree. getCurrent should return the question
	// at the root of this GameTree.
	public void reStart() {
		currentNode = root;
	}

	// Overwrite the old file for this gameTree with the current state that
	// may have new questions added since the game started. Get all other
	// method workings. Complete this method last.
	public void saveGame() {
		String txt = saveGameHelper(root);
		PrintWriter myWriter;
		try {
			myWriter = new PrintWriter(fileName);
			myWriter.write(txt);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("File not found: '" + fileName + "'");
		}
	}

	private String saveGameHelper(TreeNode node) {
		if (node == null) {
			return "";
		} else {
			return node.data + "\n" + saveGameHelper(node.left) + saveGameHelper(node.right);
		}
	}
}