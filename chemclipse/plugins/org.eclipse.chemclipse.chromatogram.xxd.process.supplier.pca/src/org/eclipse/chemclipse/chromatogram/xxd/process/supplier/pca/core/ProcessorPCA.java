/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Lorenz Gerber - PCA adapter, algorithm
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.algorithms.CalculatorNIPALS;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.algorithms.CalculatorOPLS;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.algorithms.CalculatorSVD;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.exception.MathIllegalArgumentException;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IMultivariateCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ResultPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ResultsPCA;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class ProcessorPCA {

	public <V extends IVariable, S extends ISample> ResultsPCA process(ISamplesPCA<V, S> samples, IProgressMonitor monitor) throws MathIllegalArgumentException {

		SubMonitor subMonitor = SubMonitor.convert(monitor, "Run PCA", 140);
		IAnalysisSettings analysisSettings = samples.getAnalysisSettings();
		ResultsPCA pcaResults = new ResultsPCA(analysisSettings);
		//
		try {
			/*
			 * Preprocessing
			 */
			IPreprocessingSettings preprocessingSettings = analysisSettings.getPreprocessingSettings();
			preprocessingSettings.process(samples, monitor);
			subMonitor.worked(20);
			/*
			 * Filtering
			 */
			IFilterSettings filterSettings = analysisSettings.getFilterSettings();
			filterSettings.process(samples, monitor);
			subMonitor.worked(20);
			/*
			 * Variable Extraction
			 */
			int numberOfPrincipalComponents = analysisSettings.getNumberOfPrincipalComponents();
			Algorithm algorithm = analysisSettings.getAlgorithm();
			boolean[] isSelectedVariables = selectedVariables(samples, analysisSettings);
			Map<ISample, double[]> extractData = extractData(samples, algorithm, analysisSettings, isSelectedVariables);
			setRetentionTime(pcaResults, samples, isSelectedVariables);
			int numVars = getNumSampleVars(extractData);
			subMonitor.worked(20);
			/*
			 * Prepare PCA Calculation
			 */
			IMultivariateCalculator principalComponentAnalysis = setupPCA(extractData, numVars, numberOfPrincipalComponents, algorithm);
			subMonitor.worked(20);
			/*
			 * Compute PCA
			 */
			principalComponentAnalysis.compute();
			subMonitor.worked(20);
			/*
			 * Collect PCA results
			 */
			if(!principalComponentAnalysis.getComputeStatus()) {
				return null;
			}
			subMonitor.worked(20);
			//
			List<double[]> loadingVectors = getLoadingVectors(principalComponentAnalysis, numberOfPrincipalComponents);
			double[] explainedVariances = this.getExplainedVariances(principalComponentAnalysis, numberOfPrincipalComponents);
			double[] cumulativeExplainedVariances = this.getCumulativeExplainedVariances(explainedVariances);
			pcaResults.setLoadingVectors(loadingVectors);
			pcaResults.setExplainedVariances(explainedVariances);
			pcaResults.setCumulativeExplainedVariances(cumulativeExplainedVariances);
			setEigenSpaceAndErrorValues(principalComponentAnalysis, extractData, pcaResults);
			subMonitor.worked(20);
		} finally {
			SubMonitor.done(subMonitor);
		}
		//
		return pcaResults;
	}

	@SuppressWarnings("rawtypes")
	private <V extends IVariable, S extends ISample> Map<ISample, double[]> extractData(ISamples<V, S> samples, Algorithm algorithm, IAnalysisSettings settings, boolean[] isSelectedVariable) {

		Map<ISample, double[]> selectedSamples = new HashMap<>();
		List<? extends IVariable> retentionTimes = samples.getVariables();
		/*
		 * get variables
		 */
		for(int i = 0; i < isSelectedVariable.length; i++) {
			isSelectedVariable[i] = isSelectedVariable[i] & retentionTimes.get(i).isSelected();
			if(settings.isRemoveUselessVariables()) {
				int numEmptyValues = 0;
				for(ISample sample : samples.getSampleList()) {
					if(!sample.getSampleData().get(i).isEmpty()) {
						numEmptyValues++;
					}
				}
				if(numEmptyValues <= 1) {
					isSelectedVariable[i] = false;
				}
			}
		}
		/*
		 * collect variables
		 */
		int numSelected = 0;
		for(boolean b : isSelectedVariable) {
			if(b) {
				numSelected++;
			}
		}
		final Set<String> groups = samples.getSampleList().stream().map(s -> s.getGroupName()).distinct().collect(Collectors.toList()).stream().limit(2).collect(Collectors.toSet());
		for(ISample sample : samples.getSampleList()) {
			double[] selectedSampleData = null;
			if(sample.isSelected()) {
				List<? extends ISampleData> data = sample.getSampleData();
				selectedSampleData = new double[numSelected];
				int j = 0;
				for(int i = 0; i < data.size(); i++) {
					if(isSelectedVariable[i]) {
						selectedSampleData[j] = data.get(i).getModifiedData();
						j++;
					}
				}
				selectedSamples.put(sample, selectedSampleData);
			}
		}
		//
		if(algorithm.equals(Algorithm.OPLS)) {
			Map<ISample, double[]> groupSelected = selectedSamples.entrySet().stream().filter(e -> groups.contains(e.getKey().getGroupName())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
			return groupSelected;
		}
		//
		return selectedSamples;
	}

	private <V extends IVariable, S extends ISample> boolean[] selectedVariables(ISamples<V, S> samples, IAnalysisSettings settings) {

		List<? extends IVariable> retentionTimes = samples.getVariables();
		boolean[] isSelectedVariable = new boolean[retentionTimes.size()];
		Arrays.fill(isSelectedVariable, true);
		for(int i = 0; i < isSelectedVariable.length; i++) {
			isSelectedVariable[i] = isSelectedVariable[i] & retentionTimes.get(i).isSelected();
			if(settings.isRemoveUselessVariables()) {
				int numEmptyValues = 0;
				for(ISample sample : samples.getSampleList()) {
					if(sample.isSelected()) {
						if(!sample.getSampleData().get(i).isEmpty()) {
							numEmptyValues++;
						}
					}
				}
				if(numEmptyValues <= 1) {
					isSelectedVariable[i] = false;
				}
			}
		}
		return isSelectedVariable;
	}

	private List<double[]> getLoadingVectors(IMultivariateCalculator principalComponentAnalysis, int numberOfPrincipalComponents) {

		/*
		 * Print the basis vectors.
		 */
		List<double[]> loadingVectors = new ArrayList<double[]>();
		for(int principalComponent = 0; principalComponent < numberOfPrincipalComponents; principalComponent++) {
			double[] loadingVector = principalComponentAnalysis.getLoadingVector(principalComponent);
			loadingVectors.add(loadingVector);
		}
		return loadingVectors;
	}

	private double[] getExplainedVariances(IMultivariateCalculator principalComponentAnalysis, int numberOfPrincipalComponents) {

		double summedVariance = principalComponentAnalysis.getSummedVariance();
		double[] explainedVariances = new double[numberOfPrincipalComponents];
		for(int i = 0; i < numberOfPrincipalComponents; i++) {
			explainedVariances[i] = 100.0 / summedVariance * principalComponentAnalysis.getExplainedVariance(i);
		}
		return explainedVariances;
	}

	private double[] getCumulativeExplainedVariances(double[] explainedVariances) {

		double[] cumulativeExplainedVariances = new double[explainedVariances.length];
		double cumVarTemp = 0.0;
		for(int i = 0; i < explainedVariances.length; i++) {
			cumulativeExplainedVariances[i] = cumVarTemp + explainedVariances[i];
			cumVarTemp = cumulativeExplainedVariances[i];
		}
		return cumulativeExplainedVariances;
	}

	private int getNumSampleVars(Map<ISample, double[]> extractData) {

		Iterator<Map.Entry<ISample, double[]>> it = extractData.entrySet().iterator();
		if(it.hasNext()) {
			return it.next().getValue().length;
		}
		return -1;
	}

	/**
	 * Initializes the PCA analysis.
	 *
	 * @param pcaPeakMap
	 * @param numSamples
	 * @param sampleSize
	 * @param numberOfPrincipalComponents
	 * @return PrincipalComponentAnalysis
	 * @throws Exception
	 */
	private IMultivariateCalculator setupPCA(Map<ISample, double[]> pcaPeakMap, int sampleSize, int numberOfPrincipalComponents, Algorithm algorithm) throws MathIllegalArgumentException {

		/*
		 * Initialize the PCA analysis.
		 */
		int numSamples = pcaPeakMap.size();
		IMultivariateCalculator principalComponentAnalysis = null;
		if(algorithm.equals(Algorithm.NIPALS)) {
			principalComponentAnalysis = new CalculatorNIPALS(numSamples, sampleSize, numberOfPrincipalComponents);
		} else if(algorithm.equals(Algorithm.SVD)) {
			principalComponentAnalysis = new CalculatorSVD(numSamples, sampleSize, numberOfPrincipalComponents);
		} else if(algorithm.equals(Algorithm.OPLS)) {
			principalComponentAnalysis = new CalculatorOPLS(numSamples, sampleSize, numberOfPrincipalComponents);
		}
		/*
		 * Add the samples.
		 */
		for(Map.Entry<ISample, double[]> entry : pcaPeakMap.entrySet()) {
			principalComponentAnalysis.addObservation(entry.getValue(), entry.getKey(), entry.getKey().getGroupName());
		}
		//
		return principalComponentAnalysis;
	}

	private void setEigenSpaceAndErrorValues(IMultivariateCalculator principalComponentAnalysis, Map<ISample, double[]> pcaPeakMap, IResultsPCA<IResultPCA, IVariable> pcaResults) {

		/*
		 * Set the eigen space and error membership values.
		 */
		List<IResultPCA> resultsList = new ArrayList<>();
		for(Entry<ISample, double[]> entry : pcaPeakMap.entrySet()) {
			double[] sampleData = entry.getValue();
			ISample sample = entry.getKey();
			IResultPCA pcaResult = new ResultPCA(sample);
			pcaResult.setName(sample.getName());
			pcaResult.setGroupName(sample.getGroupName());
			pcaResult.setScoreVector(principalComponentAnalysis.getScoreVector(sample));
			pcaResult.setErrorMemberShip(principalComponentAnalysis.getErrorMetric(sampleData));
			pcaResult.setSampleData(sampleData);
			resultsList.add(pcaResult);
		}
		pcaResults.getPcaResultList().clear();
		pcaResults.getPcaResultList().addAll(resultsList);
	}

	private void setRetentionTime(IResultsPCA<IResultPCA, IVariable> pcaResults, ISamples<? extends IVariable, ? extends ISample> samples, boolean[] isSelectedVariables) {

		pcaResults.getExtractedVariables().clear();
		for(int i = 0; i < samples.getVariables().size(); i++) {
			if(isSelectedVariables[i]) {
				IVariable variable = samples.getVariables().get(i);
				pcaResults.getExtractedVariables().add(variable);
			}
		}
	}
}
