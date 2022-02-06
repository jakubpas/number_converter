package net.jakubpas.converter;

import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConvertActionGroup extends ActionGroup {

	@NotNull
	@Override
	public AnAction @NotNull [] getChildren(@Nullable AnActionEvent anActionEvent) {
		return new AnAction[]{
			new ConvertAction(ConvertAction.ConvertType.DEC),
			new ConvertAction(ConvertAction.ConvertType.HEX),
			new ConvertAction(ConvertAction.ConvertType.BIN),
		};
	}
}
