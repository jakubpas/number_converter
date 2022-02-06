package net.jakubpas.converter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ConvertAction extends AnAction {

	private final static String UNKNOWN_TYPE = "Unknown type";
	private final static String CANT_CONVERT = "Can't convert";
	private final ConvertType type;

	public ConvertAction(ConvertType type) {
		super();
		this.type = type;
	}

	private String ConvertNumber(String value) {
		value = value.strip();
		int number;
		try {
			if (value.startsWith("%")) {
				value = value.substring(1);
				number = Integer.parseInt(value, 2);
			} else if (value.startsWith("$")) {
				value = value.substring(1);
				number = Integer.parseInt(value,16);
			} else {
				number = Integer.decode(value);
			}

			switch (this.type) {
				case DEC: value = Integer.toString(number); break;
				case HEX: value = "$"+Integer.toHexString(number).toUpperCase(); break;
				case BIN: value = "%"+Integer.toBinaryString(number); break;
				default: value = UNKNOWN_TYPE;
			}
		} catch (NumberFormatException e) {
			return CANT_CONVERT;
		}
		return value;
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
		final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
		CaretModel caretModel = editor.getCaretModel();
		Caret currentCaret = caretModel.getCurrentCaret();

		if (currentCaret.hasSelection()) {
			final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
			final Document document = editor.getDocument();

			Caret primaryCaret = caretModel.getPrimaryCaret();
			int selectionStart = primaryCaret.getSelectionStart();
			int selectionEnd = primaryCaret.getSelectionEnd();

			CharSequence convertedNumber = ConvertNumber(currentCaret.getSelectedText());
			WriteCommandAction.runWriteCommandAction(project, () ->
				document.replaceString(selectionStart, selectionEnd, convertedNumber)
			);
			primaryCaret.removeSelection();
		}
	}

	@Override
	public void update(AnActionEvent anActionEvent)
	{
		final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
		Caret currentCaret = editor.getCaretModel().getCurrentCaret();

		anActionEvent.getPresentation().setEnabledAndVisible(currentCaret.hasSelection());

		if (anActionEvent.getPresentation().isVisible()) {
			String value = ConvertNumber(currentCaret.getSelectedText());
			anActionEvent.getPresentation().setText(this.type.toString()+": "+value);
			anActionEvent.getPresentation().setEnabled(!value.equals(CANT_CONVERT));
		}
	}

	enum ConvertType {
		HEX{
			@Override
			public String toString() {
				return "HEX";
			}
		},
		BIN{
			@Override
			public String toString() {
				return "BIN";
			}
		},
		DEC{
			@Override
			public String toString() {
				return "DEC";
			}
		},
	}
}
