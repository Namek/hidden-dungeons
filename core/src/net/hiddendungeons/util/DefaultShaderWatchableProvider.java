package net.hiddendungeons.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

/**
 * Simply provider of DefaultShader with auto-refresh from file on Desktop platform.
 *
 */
public class DefaultShaderWatchableProvider extends BaseShaderProvider {
	static final float UPDATE_DELAY = 0.2f;//seconds
		
	private Array<ShaderInfo> _createdShaders = new Array<>();
	private float _leftTimeUntilUpdate = UPDATE_DELAY;
	private WatchService folderWatchService;

	public final DefaultShader.Config config;
	public FileHandle vertexShaderFileHandle;
	public FileHandle fragmentShaderFileHandle;


	public DefaultShaderWatchableProvider(final FileHandle vertexShader, final FileHandle fragmentShader) {
		this(new DefaultShader.Config(), vertexShader, fragmentShader);
	}

	public DefaultShaderWatchableProvider(final DefaultShader.Config config, final FileHandle vertexShader, final FileHandle fragmentShader) {
		this.config = new DefaultShader.Config(vertexShader.readString(), fragmentShader.readString());
		this.vertexShaderFileHandle = vertexShader;
		this.fragmentShaderFileHandle = fragmentShader;
		
		if (Gdx.app.getType() != ApplicationType.Desktop) {
			return;
		}

		try {
			// TODO there's stupid assumption that both vertex and fragment shaders belong to the same parent folder
			folderWatchService = FileSystems.getDefault().newWatchService();
			String workingDir = Gdx.files.getLocalStoragePath() + "bin";
			Path folder = Paths.get(workingDir, vertexShader.parent().path());
			folder.register(folderWatchService, StandardWatchEventKinds.ENTRY_MODIFY);
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	@Override
	protected Shader createShader (final Renderable renderable) {
		DefaultShader shader = new DefaultShader(renderable, config);

		_createdShaders.add(new ShaderInfo(shader, renderable));

		return shader;
	}
	
	
	public void updateAll(float deltaTime) {
		_leftTimeUntilUpdate -= deltaTime;
		boolean check = _leftTimeUntilUpdate <= 0;

		while (_leftTimeUntilUpdate <= 0) {
			_leftTimeUntilUpdate += UPDATE_DELAY;
		}
		if (!check) {
			return;
		}

		boolean updateShaders = false;
		
		WatchKey wk = null;
		
		while ((wk = folderWatchService.poll()) != null) {
			List<WatchEvent<?>> events = wk.pollEvents();
			
			for (WatchEvent<?> evt : events) {
				final Path changed = (Path) evt.context();
				String filename = changed.getFileName().toString();
				
				if (vertexShaderFileHandle.name().equals(filename) || fragmentShaderFileHandle.name().equals(filename)) {
					updateShaders = true;
					System.out.println("Update: " + filename);
					break;
				}
			}
			
			wk.reset();
		}

		if (updateShaders) {
			final String vs = vertexShaderFileHandle.readString();
			final String fs = fragmentShaderFileHandle.readString();

			// TODO remove inactive renderables, dispose inactive shaders
			boolean isCompiled = false; 
			for (int i = 0, n = _createdShaders.size; i < n; ++i) {
				ShaderInfo info = _createdShaders.get(i);
				
				String prefix = DefaultShader.createPrefix(info.renderable, config);
				ShaderProgram program = new ShaderProgram(prefix + vs, prefix + fs);

				isCompiled = program.isCompiled();
				if (!isCompiled) {
					System.out.println(program.getLog());
					break;
				}

				// Compiled? OK, Replace.
				info.shader.program.dispose();
				info.shader.program = program;
			}
			
			if (isCompiled) {
				config.vertexShader = vs;
				config.fragmentShader = fs;
			}
		}
	}
	
	private static class ShaderInfo {
		public DefaultShader shader;
		public Renderable renderable;
		
		public ShaderInfo(DefaultShader shader, Renderable renderable) {
			this.shader = shader;
			this.renderable = renderable;
		}
	}
}
