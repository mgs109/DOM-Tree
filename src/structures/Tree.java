package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		
		if(sc == null){
			return;
		}
		
		String expr = "";
		TagNode curr = null;
		
		Stack<TagNode> tagStack = new Stack<TagNode>();
		
		
		
		root = new TagNode("html", null, null);    //initializes root
		tagStack.push(root);
		
		sc.nextLine();    //puts sc on html
		
		while(sc.hasNextLine()){
			expr = sc.nextLine();      //puts sc on body when first enters loop
			
			if(expr.contains("<") && expr.contains(">") && expr.contains("/")){
				tagStack.pop();
				continue;
			}
			
			if(expr.contains("<") && expr.contains(">") && !expr.contains("/")){
				TagNode n = new TagNode(expr.replace("<", "").replace(">", ""), null, null);
				
					if(tagStack.peek().firstChild == null){
						tagStack.peek().firstChild = n;
						tagStack.push(n);
					}else{ 
						
						curr = tagStack.peek().firstChild;
						
						while(curr.sibling != null){
							curr = curr.sibling;
						}
						
						curr.sibling = n;
						tagStack.push(n);
					}
			}
			
			else{
				TagNode n = new TagNode(expr, null, null);
				if(tagStack.peek().firstChild == null){
					tagStack.peek().firstChild = n;
				}else{
					curr = tagStack.peek().firstChild;
					
					while(curr.sibling != null){
						curr = curr.sibling;
						
					}
					
					curr.sibling = n;
				}
				
			}
		}
	}
	
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		if(root == null || newTag == null || oldTag == null){
			return;
		}
		replaceTagWork(oldTag, newTag, root);
	}
	
	private void replaceTagWork(String oldTag, String newTag, TagNode curr){
		if(curr == null){
			return;
		}
		
		if(curr.tag.equals(oldTag)){
			curr.tag = newTag;
		}
		replaceTagWork(oldTag, newTag, curr.firstChild);
		replaceTagWork(oldTag, newTag, curr.sibling);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		if(row <= 0){
			return;
		}
		
		boldRowWork(row, 0, root, root.firstChild);
	}
	
	private void boldRowWork(int row, int count, TagNode prev, TagNode curr){
		if(curr == null){
			return;
		}
		
		if(curr.tag.equals("tr")){
			count++;
		}
		if(count == row && curr.tag.equals("td")){
			
			curr.firstChild = new TagNode("b", curr.firstChild, null);
			
		}
		boldRowWork(row, count, curr, curr.firstChild);
		boldRowWork(row, count, curr, curr.sibling);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if(tag == null){
			return;
		}
		
		removeTagWork(tag, root, root.firstChild);
	}
	
	
	private void removeTagWork(String tag, TagNode prev, TagNode curr){
		if(curr == null){
			return;
		}
		
		if(tag.equals("b") || tag.equals("p") || tag.equals("em")){
		
			if(curr.tag.equals(tag)){
				if(prev.firstChild == curr){
				
					prev.firstChild = curr.firstChild;
					curr.firstChild.sibling = curr.sibling;
					curr = curr.firstChild;
				}else{	
					prev.sibling = curr.firstChild;
					TagNode curr2 = curr.firstChild;
					
					while(curr2.sibling != null){
						curr2 = curr2.sibling;
					}
						curr2.sibling = curr.sibling;
						curr = prev.sibling;
				}
			}
		}
		
		if(tag.equals("ol") || tag.equals("ul")){
			if(curr.tag.equals(tag)){
				if(prev.firstChild == curr){
					
					prev.firstChild = curr.firstChild;
					TagNode curr2 = prev.firstChild;
					
					while(curr2.sibling != null || curr2.tag.equals("li")){
						if(curr2.tag.equals("li")){
							curr2.tag = "p";
							if(curr2.sibling == null){
								break;
							}
						}
						
						curr2 = curr2.sibling;
					}
					curr2.sibling = curr.sibling;
					
				}
					else{
						prev.sibling = curr.firstChild;
						TagNode curr2 = curr.firstChild;
						while(curr2.sibling != null || curr2.tag.equals("li")){
							if(curr2.tag.equals("li")){
								curr2.tag = "p";
								if(curr2.sibling == null){
									break;
								}
							}
							curr2 = curr2.sibling;
						}
						curr2.sibling = curr.sibling;
						curr = prev.sibling;
				}
			}
		}
		
		removeTagWork(tag, curr, curr.firstChild);
		removeTagWork(tag, curr, curr.sibling);
	}
	
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		
		if(tag == null || word == null || root == null){
			return;
		}
		
		addTagWork(word, tag, root, root.firstChild);
		
	}
	
	private void addTagWork(String word, String tag, TagNode prev, TagNode curr){
		
		char Char0 = '.', Char1 = ',', Char2 = '?', Char3 = '!', Char4 = ';', Char5 = ':';
		boolean match = false, add = false, midWord = true;
		
		if(curr == null){
			return;
		}
		add = true;
		if(prev.tag != tag){
			if(curr.tag.equalsIgnoreCase(word) || curr.tag.equalsIgnoreCase(word + Char0) || curr.tag.equalsIgnoreCase(word + Char1) 
				|| curr.tag.equalsIgnoreCase(word + Char2) || curr.tag.equalsIgnoreCase(word + Char3) 
				|| curr.tag.equalsIgnoreCase(word + Char4) || curr.tag.equalsIgnoreCase(word + Char5)){
				match = true;
				add = false;
				if(prev.firstChild == curr){
				
					prev.firstChild = new TagNode(tag, prev.firstChild, curr.sibling);
					curr.sibling = null;
		
					match = false;
					add = false;
			
				}
		
				if(match){
			
					prev.sibling = new TagNode(tag, curr, curr.sibling);
					curr.sibling = null;
					add = false;
				
				}
			}
		}
		
		String curr2 = curr.tag.toLowerCase();
		String word2 = word.toLowerCase();
		
		if(curr2.contains(word2) && add ){   
		
			for(int i = 0; i < curr.tag.length(); i++){
				if(curr.tag.length() == word.length() + 1){
					break;
				}
					String newWord = "";
					int j = 0, n = 0;
					
					while(curr2.charAt(i) == word2.charAt(j) && i < curr.tag.length() && j < word.length()){
						if(newWord.length() == word.length()){
							break;
						}
						
						newWord += curr.tag.charAt(i);
						i++;
						j++;
						
						if(j == word.length()){
							n= i-word.length();
							break;
						}
					}
				
					if(newWord.length() == word.length() && !curr.tag.equalsIgnoreCase(word)){
						
						if((i == curr.tag.length() || i == curr.tag.length()-1)  //if it is last word
								&& (curr.tag.substring(n, n+word.length()).equalsIgnoreCase(word) 
								|| curr.tag.substring(n+1, n+word.length()+1).equalsIgnoreCase(word)) && (curr.tag.charAt(curr.tag.length() - word.length() -2) == ' ' || curr.tag.charAt(curr.tag.length() - word.length() -1) == ' ')){
							if(prev.firstChild == curr){
								
								if(curr.tag.charAt(curr.tag.length() - 1) == Char0 || curr.tag.charAt(curr.tag.length() - 1) == Char1
									||	curr.tag.charAt(curr.tag.length() - 1) == Char2 || curr.tag.charAt(curr.tag.length() - 1) == Char3
									||	curr.tag.charAt(curr.tag.length() - 1) == Char4 || curr.tag.charAt(curr.tag.length() - 1) == Char5){
									TagNode tagWord = new TagNode(curr.tag.substring(n, n+word.length()) + curr.tag.charAt(curr.tag.length() - 1) , null, null);
									TagNode tags =  new TagNode(tag,tagWord, curr.sibling);
									TagNode firstPhrase= new TagNode(curr.tag.substring(0, n), null, tags);  
								
									
									prev.firstChild = firstPhrase;
									
									midWord = false;
								}else{
									TagNode tagWord = new TagNode(curr.tag.substring(n, n+word.length()), null, null);
									TagNode tags =  new TagNode(tag, tagWord, curr.sibling);
									TagNode firstPhrase= new TagNode(curr.tag.substring(0, n), null, tags);  
									prev.firstChild = firstPhrase;
									midWord = false;
								}	
						 }
							else{
								
								if(curr.tag.charAt(curr.tag.length() - 1) == Char0 || curr.tag.charAt(curr.tag.length() - 1) == Char1
										||	curr.tag.charAt(curr.tag.length() - 1) == Char2 || curr.tag.charAt(curr.tag.length() - 1) == Char3
										||	curr.tag.charAt(curr.tag.length() - 1) == Char4 || curr.tag.charAt(curr.tag.length() - 1) == Char5){
								
									TagNode tagWord = new TagNode(curr.tag.substring(n, n+word.length()) + curr.tag.charAt(curr.tag.length() - 1), null, null);
									TagNode tags =  new TagNode(tag,tagWord, curr.sibling);
									TagNode firstPhrase= new TagNode(curr.tag.substring(0, n), curr.firstChild, tags);  
								
									prev.sibling = firstPhrase;
									midWord = false;
								
								}else{
									
									TagNode tagWord = new TagNode(curr.tag.substring(n, n+word.length()), null, null);
									TagNode tags =  new TagNode(tag,tagWord, curr.sibling);
									TagNode firstPhrase= new TagNode(curr.tag.substring(0, n), curr.firstChild, tags);  
									
									prev.sibling = firstPhrase;
									
									midWord = false;
									
								}
							}
						}
							if(curr.tag.substring(0, word.length()).equalsIgnoreCase(word) && curr.tag.charAt(word.length()) == ' ' && i == word.length()){
								if(prev.firstChild == curr){
									TagNode secondPhrase= new TagNode(curr.tag.substring(word.length(), curr.tag.length()), null, curr.sibling);
									TagNode tagWord = new TagNode(curr.tag.substring(0, word.length()), null, null); // curr.firstchild to null
									TagNode tags =  new TagNode(tag,tagWord, secondPhrase);
									
									prev.firstChild = tags;
									
									midWord = false;
									
							 }
								else{
									TagNode secondPhrase= new TagNode(curr.tag.substring(word.length(), curr.tag.length()), null, curr.sibling);
									TagNode tagWord = new TagNode(curr.tag.substring(0, word.length()), null, null);
									TagNode tags =  new TagNode(tag,tagWord, secondPhrase);
									prev.sibling = tags;
									midWord = false;
					
								}
								
								
							}
							
							if(midWord && curr.tag.charAt(n-1) == ' ' && curr.tag.charAt(n+word.length()) == ' ' ){
								if(prev.firstChild == curr){
									TagNode tagWord = new TagNode(curr.tag.substring(n,n+ word.length()), null, null);
									TagNode secondPhrase = new TagNode(curr.tag.substring(n+word.length(), curr.tag.length()), null, curr.sibling);
									TagNode tags =  new TagNode(tag,tagWord, secondPhrase);
									TagNode firstPhrase = new TagNode(curr.tag.substring(0,n), null, tags);
									
									
									
									prev.firstChild = firstPhrase;
								
									
								}
								
								else{
									
									TagNode tagWord = new TagNode(curr.tag.substring(n,n+ word.length()), null, null);
									TagNode secondPhrase = new TagNode(curr.tag.substring(n+word.length(), curr.tag.length()), null, curr.sibling);
									TagNode tags =  new TagNode(tag,tagWord, secondPhrase);
									TagNode firstPhrase = new TagNode(curr.tag.substring(0,n), null, tags);
									
									prev.sibling = firstPhrase;
								}
							}
						}//add tag
					} //for loop		
			}
			
		addTagWork(word, tag, curr, curr.firstChild);
		addTagWork(word, tag, curr, curr.sibling);
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
}
