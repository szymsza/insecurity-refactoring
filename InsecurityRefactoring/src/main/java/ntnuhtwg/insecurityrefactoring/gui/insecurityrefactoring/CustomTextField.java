package ntnuhtwg.insecurityrefactoring.gui.insecurityrefactoring;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

// Source: https://lazicbrano.wordpress.com/2013/08/01/jtextfield-placeholder/
public class CustomTextField extends JTextField {

    private Font originalFont;
    private Color originalForeground;
    /**
     * Grey by default*
     */
    private Color placeholderForeground = new Color(160, 160, 160);
    private boolean textWrittenIn;

    /**
     * You can insert all constructors.
     * I inserted only this one.*
     */
    public CustomTextField() {
        super();
        this.originalFont = getFont();
    }
    public CustomTextField(String defaulText) {
        super(defaulText);
        this.originalFont = getFont();
    }
    public CustomTextField(int columns) {
        super(columns);
        this.originalFont = getFont();
    }

    @Override
    public void setFont(Font f) {
        super.setFont(f);
        if (isTextWrittenIn()) {
            originalFont = f;
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (isTextWrittenIn()) {
            originalForeground = fg;
        }
    }

    public Color getPlaceholderForeground() {
        return placeholderForeground;
    }

    public void setPlaceholderForeground(Color placeholderForeground) {
        this.placeholderForeground = placeholderForeground;
    }

    public boolean isTextWrittenIn() {
        return textWrittenIn;
    }

    public void setTextWrittenIn(boolean textWrittenIn) {
        this.textWrittenIn = textWrittenIn;
    }

    public void setPlaceholder(final String text) {

        this.customizeText(text);

        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (getText().trim().length() != 0) {
                    setTextWrittenIn(true);
                    setFont(originalFont);
                    setForeground(originalForeground);
                }

            }
        });

        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!isTextWrittenIn()) {
                    setText("");
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().trim().length() == 0) {
                    customizeText(text);
                }
            }

        });

    }

    private void customizeText(String text) {
        setText(text);
        /**If you change font, family and size will follow
         * changes, while style will always be italic**/
        setTextWrittenIn(false);
        setFont(new Font(getFont().getFamily(), Font.ITALIC, getFont().getSize()));
        setForeground(getPlaceholderForeground());
    }

}