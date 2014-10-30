package eu.eco2clouds.scheduler.bonfire;

import java.util.List;

import eu.eco2clouds.accounting.datamodel.parser.VM;
import eu.eco2clouds.api.bonfire.occi.datamodel.Experiment;
import eu.eco2clouds.api.bonfire.occi.datamodel.Compute;

/**
 * 
 * Copyright 2014 ATOS SPAIN S.A. 
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

public interface BFClientScheduler {
	public Experiment getExperiment(String userId, long experimentId);
	public List<Compute> getVMsOfExperiment(String userId, long experimentId);
	public Compute getVM(String userId, String href);
	public Compute shutdownVM(String userId, Compute compute);
	public Compute suspendVM(String userId, Compute compute);
	public Compute cancelVM(String userId, Compute compute);
	public Compute resumenVM(String userId, Compute compute);
	public Compute stopVM(String userId, Compute compute);
	public VM stopVM(VM vm);
	public VM resumeVM(VM vm);
	public VM cancelVM(VM vm);
	public VM suspendVM(VM vm);
	public VM shutdownVM(VM vm);
}
