package com.g3g4x5x6.ui.embed.editor.provider;

import lombok.extern.slf4j.Slf4j;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;

@Slf4j
public class BashCompletionProvider extends DefaultCompletionProvider {

    public BashCompletionProvider(){
        this.setAutoActivationRules(true, "abcdefghijklmnopqrstuvwxyz.");
        initKeyWord();
        initShortHand();
        log.debug("Load BashCompletionProvider");
    }

    private void initKeyWord(){
        // Add completions for all Bash keywords. A BasicCompletion is just
        // a straightforward word completion.
        this.addCompletion(new BasicCompletion(this, "alias "));
        this.addCompletion(new BasicCompletion(this, "continue "));
        this.addCompletion(new BasicCompletion(this, "echo "));
        this.addCompletion(new BasicCompletion(this, "exit "));
        // ... etc ...
        this.addCompletion(new BasicCompletion(this, "fg"));
        this.addCompletion(new BasicCompletion(this, "test "));
        this.addCompletion(new BasicCompletion(this, "umask "));
        this.addCompletion(new BasicCompletion(this, "return "));
        this.addCompletion(new BasicCompletion(this, "read "));
    }

    private void initShortHand(){
        // Add a couple of "shorthand" completions. These completions don't
        // require the input text to be the same thing as the replacement text.
        this.addCompletion(new ShorthandCompletion(this, "if",
                "if COMMANDS; then COMMANDS; fi",
                "if COMMANDS; then COMMANDS; [ elif COMMANDS; then COMMANDS; ]... >"));
        this.addCompletion(new ShorthandCompletion(this, "for",
                "for (( exp1; exp2; exp3 )); do COMMANDS; done",
                "for (( exp1; exp2; exp3 )); do COMMANDS; done"));
        this.addCompletion(new ShorthandCompletion(this, "while",
                "while COMMANDS; do COMMANDS; done",
                "while COMMANDS; do COMMANDS; done"));
        this.addCompletion(new ShorthandCompletion(this, "func",
                "function name { COMMANDS ; }",
                "function name { COMMANDS ; } or name () { COMMANDS ; }"));
    }
}
