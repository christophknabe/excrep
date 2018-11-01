package exercise;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import org.junit.Test;

import exercise.FileService;
import exercise.FileService.CountFailure;

/**Test driver for class {@linkplain FileService}*/
public class FileServiceTest {

	private final FileService fileService = new FileService();
	
	@Test
	public void count10Lines() throws IOException {
		final File tempFile = File.createTempFile(getClass().getSimpleName(), ".txt");
		try(final PrintWriter out = new PrintWriter(new FileOutputStream(tempFile))){
			for(int i=1; i<=10; i++) {
				out.print("Line ");
				out.println(i);
			}
		}
		try(final BufferedReader in = new BufferedReader(new FileReader(tempFile))){
			for(int i=1; i<=10; i++) {
				final String line = in.readLine();
				assertEquals("Line " + i, line);
			}
			final String afterLine = in.readLine();
			assertNull(afterLine);
		}
		assertEquals(10, fileService.countLines(tempFile));
	}
	
	@Test
	public void countInNonexistingFile() {
		final File file = new File(UUID.randomUUID().toString());
		try {
			fileService.countLines(file);
			fail("CountFailure expected");
		}catch(CountFailure expected) {
			final Object[] parameters = expected.getParameters();
			final String className = expected.getClass().getSimpleName();
			assertTrue(className + " must have a Throwable as cause!", expected.getCause() instanceof Throwable);
			assertNotNull(className + " must have parameters!", parameters);
			assertTrue(className + " must have at least 1 parameter!", parameters.length>=1);
			assertEquals(className + " must have the filename as parameter!", file, parameters[0]);
		}
	}

}
