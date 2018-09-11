package simvasos.scenario.mciresponse;

import simvasos.simulation.Simulator;
import simvasos.simulation.analysis.Snapshot;
import simvasos.simulation.component.Scenario;
import simvasos.scenario.mciresponse.MCIResponseScenario.SoSType;
import simvasos.simulation.component.World;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MCIResponseRunner {
    public static void main(String[] args) throws InterruptedException {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String testSession = "ABCPlus_AllTypes";

        // 테스트 커밋
        // 수정 --> Tick의 총 횟수
        //int endTick = 7500; // 8000
        int endTick = 1000;

        int minTrial = 1;

        // 수정 --> 각 케이스별로 돌리는 횟수
        //int maxTrial = 100;
        int maxTrial = 30;

        try {
            File simulationLogFile = new File(String.format("traces/" + testSession + "/" + testSession + "_simulation_logs.csv"));

            BufferedWriter simulationLogWriter = new BufferedWriter(new FileWriter(simulationLogFile, true));

//            simulationLogWriter.write("nPatient,nFireFighter,SoSType,Duration,MessageCount");
//            simulationLogWriter.newLine();

            // nPatient, nFireFighter

            // 수정 (원본)
            //SoSType[] targetTypeArray = {SoSType.Virtual, SoSType.Collaborative, SoSType.Acknowledged, SoSType.Directed};

            SoSType[] targetTypeArray = {SoSType.Collaborative};

            // 수정 (원본)
            //int[] nPatientArray = {50, 100, 150, 200, 250};
            //int[] nFireFighterArray = {2, 5, 10, 25, 50};

            int[] nPatientArray = {14};
            int[] nFireFighterArray = {4};

            ArrayList<Snapshot> trace;
            long startTime;
            long duration;      // 매 케이스 별로 시스템 실행 시간
            long durationSum;   // 각각의 케이스별 시스템 실행 시간의 총합
            int messageCnt;
            int messageCntSum;

            for (int nPatient : nPatientArray) {
                for (int nFireFighter : nFireFighterArray) {
                    for (SoSType sostype : targetTypeArray) {
                        System.out.println("Patient: " + nPatient + ", Firefighter: " + nFireFighter + ", SoS: " + sostype);
                        System.out.println(datetimeFormat.format(new Date()));

                        Scenario scenario = new MCIResponseScenario(sostype, nPatient, nFireFighter, 0, 10);
                        World world = scenario.getWorld();

                        durationSum = 0;
                        messageCntSum = 0;
                        //world.setSeed(new Random().nextLong());
                        for (int i = minTrial - 1; i <= maxTrial; i++) {
                            world.setSeed(new Random().nextLong());
                            ((MCIResponseWorld) world).setSoSType(sostype);

                            startTime = System.currentTimeMillis();

                            trace = Simulator.execute(world, endTick);      // 여기서 들어가서 fault를 넣어야 할듯. 아니면 message쪽까지 가서 해야하나?

                            if (i == minTrial - 1)
                                continue;

                            duration = (System.currentTimeMillis() - startTime);
                            durationSum += duration;
                            messageCnt = (int) world.getCurrentSnapshot().getProperties().get(1).value;
                            messageCntSum += messageCnt;

                            simulationLogWriter.write(nPatient + "," + nFireFighter + "," + sostype.toString() + "," + duration + "," + messageCnt);
                            simulationLogWriter.newLine();

                            writeTrace(trace, String.format("traces/%s/%04d_%03d_%s_%04d.txt", testSession, nPatient, nFireFighter, sostype, i));
                        }

                        simulationLogWriter.flush();
                        System.out.println("Average duration: " + durationSum / (maxTrial - minTrial + 1));
                        System.out.println("Average messageCnt: " + messageCntSum / (maxTrial - minTrial + 1));
                    }
                }
            }

            simulationLogWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Session complete: " + datetimeFormat.format(new Date()));
    }

    private static void writeTrace(List<Snapshot> trace, String filename) {
        try {
            File file = new File(filename);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            for (Snapshot snapshot : trace) {
                bw.write((snapshot.getProperties().get(0).value).toString());
                bw.newLine();
            }

            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
