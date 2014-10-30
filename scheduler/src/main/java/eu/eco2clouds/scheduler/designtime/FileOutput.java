package eu.eco2clouds.scheduler.designtime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * Copyright 2014 University of Manchester 
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class FileOutput
{
	private static BufferedWriter out = null;
	
	public static synchronized void outputToFile(String output)
	{
		try {
			out = new BufferedWriter(new FileWriter("UWOUT.txt", true));
			out.append(output);
			//out.newLine();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeFile()
	{
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
