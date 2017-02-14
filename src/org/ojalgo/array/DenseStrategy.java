package org.ojalgo.array;

import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.random.Distribution;

/**
 * To be used by implementations that delegate to a DenseArray
 *
 * @author apete
 */
final class DenseStrategy<N extends Number> {

    static long DEFAULT = 16_384L;
    static long MINIMAL = 16L;

    private long myChunk = DEFAULT;
    private final DenseArray.Factory<N> myDenseFactory;
    private long myInitial = MINIMAL;

    DenseStrategy(DenseArray.Factory<N> denseFactory) {

        super();

        myDenseFactory = denseFactory;
    }

    DenseStrategy<N> capacity(Distribution expected) {
        final long stdDev = (long) expected.getStandardDeviation();
        final long exp = (long) expected.getExpected();
        return this.chunk(stdDev).initial(exp + stdDev);
    }

    DenseStrategy<N> chunk(long chunk) {
        int power = PrimitiveMath.powerOf2Smaller(chunk);
        myChunk = Math.max(MINIMAL, 1L << power);
        return this;
    }

    long grow(long current) {

        long required = current + 1L;

        long retVal = myChunk;

        if (required >= myChunk) {
            while (retVal < required) {
                required += myChunk;
            }
        } else {
            long maybe = retVal / 2L;
            while (maybe >= required) {
                retVal = maybe;
                maybe /= 2L;
            }
        }

        return retVal;
    }

    DenseStrategy<N> initial(long initial) {
        myInitial = Math.max(MINIMAL, initial);
        return this;
    }

    DenseArray<N> make(long size) {
        return myDenseFactory.make(size);
    }

    DenseArray<N> makeChunk() {
        return this.make(myChunk);
    }

    DenseArray<N> makeInitial() {
        return this.make(myInitial);
    }

}