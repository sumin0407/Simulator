package simvasos.scenario.faultscenario;

import simvasos.simulation.analysis.Snapshot;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Statistics {
    class StatisticsNode {
        String sosType;
        int pCount;     // 환자 수
        int ffCount;    // 소방관 수
        int delay;
        ArrayList<Snapshot> trace;

        public StatisticsNode(String sosType, int pCount, int ffCount, int delay, ArrayList<Snapshot> trace) {
            this.sosType = sosType;
            this.pCount = pCount;
            this.ffCount = ffCount;
            this.delay = delay;
            this.trace = trace;
        }
    }

    ArrayList<StatisticsNode> nodes = new ArrayList<StatisticsNode>();

    public void add(StatisticsNode node) {
        nodes.add(node);
    }
    public void add(String sosType, int pCount, int ffCount, int delay, ArrayList<Snapshot> trace) {
        add(new StatisticsNode(sosType, pCount, ffCount, delay, trace));
    }

    public void write(BufferedWriter writer) throws IOException {
//        for (Snapshot snapshot : trace) {
//            bw.write((snapshot.getProperties().get(0).value).toString());
//            bw.newLine();
//        }

        for(int i = 0; i < nodes.size(); ++i) {
            StatisticsNode node = nodes.get(i);
            String content = node.sosType;
            if(i < nodes.size() - 1) {
                content += ", ";
            }
            writer.write(content);
        }
        writer.newLine();

        for(int i = 0; i < nodes.size(); ++i) {
            StatisticsNode node = nodes.get(i);
            String content = "P: " + String.valueOf(node.pCount) + " / FF: " + String.valueOf(node.ffCount);
            if(i < nodes.size() - 1) {
                content += ", ";
            }
            writer.write(content);
        }
        writer.newLine();

        for(int i = 0; i < nodes.size(); ++i) {
            StatisticsNode node = nodes.get(i);
            String content = "delay: " + String.valueOf(node.delay);
            if(i < nodes.size() - 1) {
                content += ", ";
            }
            writer.write(content);
        }
        writer.newLine();

        for(int trace_index = 0; trace_index < nodes.get(0).trace.size(); trace_index++) {
            for (int i = 0; i < nodes.size(); ++i) {
                StatisticsNode node = nodes.get(i);
                ArrayList<Snapshot> trace = node.trace;
                Snapshot snapshot = trace.get(trace_index);
                String content = (snapshot.getProperties().get(0).value).toString();
                if (i < nodes.size() - 1) {
                    content += ", ";
                }
                writer.write(content);
            }
            writer.newLine();
        }
        //writer.newLine();

        writer.flush();
    }
}
