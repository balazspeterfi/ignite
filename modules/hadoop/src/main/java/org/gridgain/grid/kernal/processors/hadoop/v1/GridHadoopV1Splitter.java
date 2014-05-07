/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.kernal.processors.hadoop.v1;

import org.apache.hadoop.mapred.*;
import org.gridgain.grid.*;
import org.gridgain.grid.hadoop.*;
import org.gridgain.grid.kernal.processors.hadoop.v2.*;

import java.io.*;
import java.util.*;

/**
 * Hadoop API v1 splitter.
 */
public class GridHadoopV1Splitter {
    /**
     * @param jobConf Job configuration.
     * @return Collection of mapped blocks.
     * @throws GridException If mapping failed.
     */
    public static Collection<GridHadoopInputSplit> splitJob(JobConf jobConf) throws GridException {
        try {
            InputFormat<?, ?> format = jobConf.getInputFormat();

            assert format != null;

            InputSplit[] splits = format.getSplits(jobConf, 0);

            Collection<GridHadoopInputSplit> res = new ArrayList<>(splits.length);

            for (InputSplit nativeSplit : splits) {
                if (nativeSplit instanceof FileSplit) {
                    FileSplit s = (FileSplit)nativeSplit;

                    res.add(new GridHadoopFileBlock(s.getLocations(), s.getPath().toUri(), s.getStart(), s.getLength()));
                }
                else
                    res.add(new GridHadoopSplitWrapper(nativeSplit, nativeSplit.getLocations()));
            }

            return res;
        }
        catch (IOException e) {
            throw new GridException(e);
        }
    }
}
