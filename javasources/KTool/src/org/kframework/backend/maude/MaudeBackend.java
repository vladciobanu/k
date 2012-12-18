package org.kframework.backend.maude;

import org.kframework.backend.BasicBackend;
import org.kframework.kil.Definition;
import org.kframework.kil.loader.DefinitionHelper;
import org.kframework.utils.Stopwatch;
import org.kframework.utils.file.FileUtil;
import org.kframework.utils.general.GlobalSettings;

import java.io.IOException;

public class MaudeBackend extends BasicBackend {

	public MaudeBackend(Stopwatch sw) {
		super(sw);
	}

	@Override
	public void run(Definition definition) throws IOException {
		MaudeFilter maudeFilter = new MaudeFilter();
		definition.accept(maudeFilter);

		String maudified = maudeFilter.getResult();

		FileUtil.saveInFile(DefinitionHelper.dotk.getAbsolutePath() + "/def.maude", maudified);
		if (GlobalSettings.verbose)
			sw.printIntermediate("Generating Maude file");

	}

	@Override
	public String getDefaultStep() {
		return "LastStep";
	}
}
