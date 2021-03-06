package fr.adrienbrault.idea.symfony2plugin;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.CaretListener;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.caret.overlay.CaretTextOverlayListener;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class SymfonyApplicationComponent implements ApplicationComponent {

    private final EditorFactory editorFactory;
    private CaretListener overlayerCaretListener;

    public SymfonyApplicationComponent(@NotNull EditorFactory editorFactory) {
        this.editorFactory = editorFactory;
    }

    @Override
    public void initComponent() {
        editorFactory.getEventMulticaster().addCaretListener(this.overlayerCaretListener = new CaretTextOverlayListener());
    }

    @Override
    public void disposeComponent() {
        if(overlayerCaretListener != null) {
            editorFactory.getEventMulticaster().removeCaretListener(overlayerCaretListener);
        }
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "SymfonyApplicationComponent";
    }
}
