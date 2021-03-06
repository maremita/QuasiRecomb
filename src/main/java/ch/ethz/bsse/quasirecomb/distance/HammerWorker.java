/**
 * Copyright (c) 2011-2013 Armin Töpfer
 *
 * This file is part of QuasiRecomb.
 *
 * QuasiRecomb is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * QuasiRecomb is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * QuasiRecomb. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.ethz.bsse.quasirecomb.distance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

/**
 * @author Armin Töpfer (armin.toepfer [at] gmail.com)
 */
public class HammerWorker extends RecursiveTask<Map<String, Map<String, Integer>>> {

    private static final long serialVersionUID = 1L;
    private String[] fasta;
    private int start;
    private int end;

    public HammerWorker(String[] fasta, int start, int end) {
        this.start = start;
        this.end = end;
        this.fasta = fasta;
    }

    @Override
    protected Map<String, Map<String, Integer>> compute() {
        if (end - start <= 1) {
//            DistanceUtils.calcHamming(null, null);
            Map<String, Map<String, Integer>> map = new HashMap<>();
            for (int i = start; i < end; i++) {
//                Map<String, Integer> mapInner = Globals.getINSTANCE().getFjPool().invoke(new HammerInnerWorker(fasta, i, 0, fasta.length));
                Map<String, Integer> mapInner = new HashMap<>();
                for (int j = 0; j < fasta.length; j++) {
//                    if (i != j) {
                        mapInner.put(fasta[j], DistanceUtils.calcHamming(fasta[i], fasta[j]));
//                    }
                }
//                StatusUpdate.getINSTANCE().printHamming(fasta.length);
                map.put(fasta[i], mapInner);
            }
            return map;
        } else {
            final int mid = start + (end - start) / 2;
            HammerWorker left = new HammerWorker(fasta, start, mid);
            HammerWorker right = new HammerWorker(fasta, mid, end);
            left.fork();
            Map<String, Map<String, Integer>> compute = right.compute();
            compute.putAll(left.join());
            return compute;
        }
    }
}
