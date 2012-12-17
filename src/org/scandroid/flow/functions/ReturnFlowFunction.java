/*
 *
 * Copyright (c) 2009-2012,
 *
 *  Galois, Inc. (Aaron Tomb <atomb@galois.com>, Rogan Creswick <creswick@galois.com>)
 *  Steve Suh    <suhsteve@gmail.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */
package org.scandroid.flow.functions;

import org.scandroid.domain.CodeElement;
import org.scandroid.domain.DomainElement;
import org.scandroid.domain.IFDSTaintDomain;
import org.scandroid.domain.LocalElement;
import org.scandroid.domain.ReturnElement;

import com.ibm.wala.dataflow.IFDS.IUnaryFlowFunction;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.util.intset.IntSet;
import com.ibm.wala.util.intset.MutableSparseIntSet;
import com.ibm.wala.util.intset.SparseIntSet;

/**
 * @author creswick
 * 
 */
public class ReturnFlowFunction<E extends ISSABasicBlock> implements
		IUnaryFlowFunction {

	private final IFDSTaintDomain<E> domain;
	private final CodeElement ce;

	/**
	 * @param domain
	 * @param def
	 *            of the invoke instruction we're returning to
	 */
	public ReturnFlowFunction(IFDSTaintDomain<E> domain, int def) {
		this.domain = domain;
		this.ce = new LocalElement(def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wala.dataflow.IFDS.IUnaryFlowFunction#getTargets(int)
	 */
	@Override
	public IntSet getTargets(int d1) {
		if (0 == d1) {
			return TaintTransferFunctions.ZERO_SET;
		}

		DomainElement de = domain.getMappedObject(d1);
		// if the domain element is a return element, propagate its taint
		if (de.codeElement instanceof ReturnElement) {
			return SparseIntSet.singleton(domain
					.getMappedIndex(new DomainElement(ce, de.taintSource)));
		}
		return TaintTransferFunctions.EMPTY_SET;
	}
}