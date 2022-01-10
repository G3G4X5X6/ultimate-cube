package com.g3g4x5x6.ui.embed.editor.provider;

import lombok.extern.slf4j.Slf4j;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;

@Slf4j
public class JavaCompletionProvider extends DefaultCompletionProvider {

    public JavaCompletionProvider() {
        this.setAutoActivationRules(true, "abcdefghijklmnopqrstuvwxyz.");
        initKeyWord();
        initShortHand();
        log.debug("Load JavaCompletionProvider");
    }

    private void initKeyWord() {
        // Add completions for all Bash keywords. A BasicCompletion is just
        // a straightforward word completion.
        this.addCompletion(new BasicCompletion(this, "abstract"));
        this.addCompletion(new BasicCompletion(this, "assert"));
        this.addCompletion(new BasicCompletion(this, "break"));
        this.addCompletion(new BasicCompletion(this, "case"));
        // ... etc ...
        this.addCompletion(new BasicCompletion(this, "transient"));
        this.addCompletion(new BasicCompletion(this, "try"));
        this.addCompletion(new BasicCompletion(this, "void"));
        this.addCompletion(new BasicCompletion(this, "volatile"));
        this.addCompletion(new BasicCompletion(this, "while"));
    }

    private void initShortHand() {
        // Add a couple of "shorthand" completions. These completions don't
        // require the input text to be the same thing as the replacement text.
        this.addCompletion(new ShorthandCompletion(this, "sysout",
                "System.out.println(", "System.out.println("));
        this.addCompletion(new ShorthandCompletion(this, "syserr",
                "System.err.println(", "System.err.println("));
    }
}
