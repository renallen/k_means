package com.company;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;


public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        dataset Datamat= new dataset("src/com/company/testSet.txt",4);
        

        System.out.printf("数据总量：%d,分为%d类\n",Datamat.len,Datamat.cluster);
        for (int i=0;i<Datamat.cluster;i++){
            System.out.printf("第%d类中心点",i+1);
            for (int j=0;j<Datamat.wide;j++){
                System.out.printf("%f, ",Datamat.centriods[i][j]);
            }
            System.out.printf("数量：%d\n",Datamat.class_len[i]);
        }
        for (int i=0;i<Datamat.len;i++){
            System.out.printf("%d:属于第%d类，距离为%f\n",i+1,Math.round(Datamat.clusterAssment[i][0]+1),Datamat.clusterAssment[i][1]);}


        Datamat.plot_data();
    }

    //两点之间的距离
}

class dataset {
    static double [][] Datamat;
    static int len;
    static int wide;
    static double [][] centriods;
    static int cluster;
    static double [][]  clusterAssment;
    static int[] class_len;
    dataset(String filename ,int k){
        cluster=k;
        load_data(filename);
        centriods=new double[cluster][wide];
        clusterAssment=new double[len][2];
        randcent( cluster);
        kMeans(Datamat);
        class_len=new int[cluster];
        for (int i=0;i<cluster;i++){

            for (int j=0;j<len;j++){
                if (clusterAssment[j][0]==i){
                    class_len[i]++;
                }
            }
        }
    }

    static void load_data(String filename){

        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String str="";
            String s;
            int i=0;
            while ((s = in.readLine()) != null) {
                str=str+s+"\n";
                i++;
            }
            in.close();
            len=i;
            //System.out.println(len);
            Datamat=new double[i][2];
            String[] st = str.split("\n");
            String [] st2;
            for (i=0;i<len;i++) {
                st2= st[i].split("\t");
                wide=st2.length;
                for (int j=0;j<wide;j++) {
                    Datamat[i][j] = Double.parseDouble(st2[j]);
                }
                //System.out.printf("%f,%f\n",Datamat[i][0],Datamat[i][1]);
            }

        } catch (IOException e) {
        }
    }

    static void randcent( int k){

        double[] minj=new double[wide];
        double[] maxj=new double[wide];
        int i,j;
        for ( i=0; i<len;i++){
            for ( j=0;j<wide;j++){

                if(minj[j]>Datamat[i][j])
                    minj[j]=Datamat[i][j];

                if(maxj[j]<Datamat[i][j])
                    maxj[j]=Datamat[i][j];
            }
        }
        for (i=0;i<k;i++) {
            for (j = 0; j < wide; j++)
                centriods[i][j] = (maxj[j] - minj[j]) *(i+1)/k + minj[j];
        }
    }
    static double distEclud(double[] A, double[] B){
        int len=A.length;
        float result=0;
        for (int i=0;i<len;i++){
            result+=Math.pow(A[i]-B[i],2);
        }
        return Math.sqrt(result);
    }

    static void kMeans(double [][] datamat){
        boolean clusterChanged=true;
        double minDist,distJ;
        int minInd;
//        int iter=0;
//        final int m=3;
        while (clusterChanged ){
            clusterChanged=false;
            for (int i=0;i<len;i++){

                minDist=Double.MAX_VALUE;
                minInd=-1;
                for (int j=0;j<cluster;j++){
                    distJ= distEclud(centriods[j],datamat[i]);
                    if (minDist>distJ){
                        minDist=distJ;
                        minInd=j;
                    }
                }
                if (clusterAssment[i][0]!=minInd ){
                    clusterChanged = true;
                }

                clusterAssment[i][0]= minInd;
                clusterAssment[i][1]=minDist*minDist;
            }
            //检测到3次没有变化了

//            if( clusterChanged==false){
//                iter++;
//                clusterChanged=true;
//            }
            double [][] centroid_tmp=new double[cluster][wide];
            int k=0;
            for (int i=0;i<len;i++){
                for (int j=0;j<cluster;j++){
                    if (clusterAssment[i][0]==j){
                        for (int l=0;l<wide;l++) {
                            centroid_tmp[j][l]= ((centroid_tmp[j][l])*k + datamat[i][l])/(k+1);
                        }
                        k++;
                    }
                }
            }
            for (int i=0;i<cluster;i++){
                for (int j=0;j<wide;j++){
                    centriods[i][j]= centroid_tmp[i][j];
                }
            }
        }
    }
    static void plot_data(){
        DefaultXYDataset xydataset = new DefaultXYDataset();
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
        mChartTheme.setExtraLargeFont(new Font("黑体", Font.BOLD, 20));
        //设置轴向字体
        mChartTheme.setLargeFont(new Font("宋体", Font.CENTER_BASELINE, 15));
        //设置图例字体
        mChartTheme.setRegularFont(new Font("宋体", Font.CENTER_BASELINE, 15));
        //应用主题样式
        ChartFactory.setChartTheme(mChartTheme);
        for (int l=0;l<cluster;l++) {
            double[][] datas = new double[2][class_len[l]];
            int k=0;
            for (int i = 0; i < len; i++) {

                if (clusterAssment[i][0] == l) {
                        datas[0][k] = Datamat[i][0];
                        datas[1][k] = Datamat[i][1];
                        k++;

                }
            }

//            for (int i=0;i<datas[0].length;i++){
//                System.out.printf("%d,%f,%f\n",l,datas[0][i],datas[1][i]);}
            xydataset.addSeries(l, datas);  //l为类别标签}
        }
        for (int i=0; i<cluster;i++){
        	
            double [][] data=new double[centriods[0].length][1];
            for (int j=0;j<centriods[0].length;j++)
            data[j][0]=centriods[i][j];
            xydataset.addSeries(i+cluster, data);  //l为类别标签}

        }
        JFreeChart chart = ChartFactory.createScatterPlot("分类", "GR", "CNL", xydataset, PlotOrientation.VERTICAL, true, false, false);
        ChartFrame frame = new ChartFrame("散点图", chart, true);
        chart.setBackgroundPaint(Color.white);
        chart.setBorderPaint(Color.GREEN);
        chart.setBorderStroke(new BasicStroke(1.5f));
        XYPlot xyplot = (XYPlot) chart.getPlot();

        xyplot.setBackgroundPaint(new Color(255, 253, 246));
        ValueAxis vaaxis = xyplot.getDomainAxis();
        vaaxis.setAxisLineStroke(new BasicStroke(1.5f));

        ValueAxis va = xyplot.getDomainAxis(0);
        va.setAxisLineStroke(new BasicStroke(1.5f));

        va.setAxisLineStroke(new BasicStroke(1.5f));    // 坐标轴粗细
                va.setAxisLinePaint(new Color(215, 215, 215));    // 坐标轴颜色
        xyplot.setOutlineStroke(new BasicStroke(1.5f));   // 边框粗细
        va.setLabelPaint(new Color(10, 9, 10));          // 坐标轴标题颜色
        va.setTickLabelPaint(new Color(102, 102, 102));   // 坐标轴标尺值颜色
        ValueAxis axis = xyplot.getRangeAxis();
        axis.setAxisLineStroke(new BasicStroke(1.5f));

        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot
                .getRenderer();
        xylineandshaperenderer.setSeriesOutlinePaint(0, Color.WHITE);
        xylineandshaperenderer.setUseOutlinePaint(true);
        NumberAxis numberaxis = (NumberAxis) xyplot.getDomainAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        numberaxis.setTickMarkInsideLength(2.0F);
        numberaxis.setTickMarkOutsideLength(0.0F);
        numberaxis.setAxisLineStroke(new BasicStroke(1.5f));

        frame.pack();
        frame.setVisible(true);
    }

}

