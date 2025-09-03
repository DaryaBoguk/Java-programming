package lab6.server;

import java.util.EventObject;

public class WordFoundEvent extends EventObject {
  private final String word;
  private final int line;

  public WordFoundEvent(Object source, String word, int lineNum) {
    super(source);
    this.word = word;
    this.line = lineNum;
  }

  public String getWord() {
    return word;
  }

  public int getLineNum() {
    return line;
  }
}
