Kubernetes & Light Weight Kubernetes Performance Test
===

## Performance Test Configuration

|                         |   publisher/subscriber   |
| :---------------------: | :----------------------: |
|         **QoS**         | RELIABLE_RELIABILITY_QOS |
|       **Topics**        |      Recloser Topic      |
|    **Sample Count**     |          1,000           |
|  **Data Buffer Bytes**  |          5K~50K          |
|   **Transport Type**    |         RTPS_UDP         |
|         **CNI**         |      Calico v3.26.1      |
| **Sleep time Per Data** |           10ms           |

## Average RTT per Data (ms) 
| RTI DDS(K8s) |   1    |   2    |   3    |   4    |   5    |   Average   | Average(no Sleep time) |
| :----------: | :----: | :----: | :----: | :----: | :----: | :---------: | :--------------------: |
|    **5K**    | 10.473 | 10.422 | 10.425 | 10.489 | 10.392 | **10.4402** |       **0.4402**       |
|   **10K**    | 10.429 | 10.443 | 10.433 | 10.452 | 10.443 |  **10.44**  |        **0.44**        |
|   **15K**    | 10.498 | 10.484 | 10.483 | 10.483 | 10.486 | **10.4868** |       **0.4868**       |
|   **20K**    | 10.524 | 10.550 | 10.532 | 10.619 | 10.526 | **10.5502** |       **0.5502**       |
|   **25K**    | 10.572 | 10.581 | 10.583 | 10.573 | 10.596 | **10.581**  |       **0.581**        |
|   **30K**    | 10.618 | 10.613 | 10.616 | 10.620 | 10.618 | **10.617**  |       **0.617**        |
|   **35K**    | 10.660 | 10.763 | 10.648 | 10.643 | 10.654 | **10.6736** |       **0.6736**       |
|   **40K**    | 10.807 | 10.733 | 10.702 | 10.719 | 10.828 | **10.7578** |       **0.7578**       |
|   **45K**    | 10.769 | 10.765 | 10.737 | 10.736 | 10.727 | **10.7468** |       **0.7468**       |
|   **50K**    | 10.758 | 10.755 | 10.765 | 10.760 | 10.755 | **10.7586** |       **0.7586**       |

| Open DDS(K8s) |   1    |   2    |   3    |   4    |   5    |   Average   | Average(no Sleep time) |
| :-----------: | :----: | :----: | :----: | :----: | :----: | :---------: | :--------------------: |
|    **5K**     | 11.683 | 11.668 | 11.783 | 11.721 | 11.684 | **11.7078** |       **1.7078**       |
|    **10K**    | 11.852 | 11.821 | 11.864 | 11.828 | 11.823 | **11.8376** |       **1.8376**       |
|    **15K**    | 11.989 | 11.991 | 12.080 | 11.975 | 11.986 | **12.0042** |       **2.0042**       |
|    **20K**    | 12.110 | 12.073 | 12.074 | 12.116 | 12.114 | **12.0974** |       **2.0974**       |
|    **25K**    | 12.302 | 12.697 | 12.279 | 12.396 | 12.291 | **12.393**  |       **2.393**        |
|    **30K**    | 12.441 | 12.378 | 12.714 | 12.498 | 12.476 | **12.5014** |       **2.5014**       |
|    **35K**    | 12.530 | 12.563 | 12.551 | 12.547 | 12.820 | **12.6022** |       **2.6022**       |
|    **40K**    | 13.051 | 12.674 | 12.663 | 12.731 | 12.658 | **12.7554** |       **2.7554**       |
|    **45K**    | 12.835 | 12.843 | 12.881 | 12.902 | 12.865 | **12.8652** |       **2.8652**       |
|    **50K**    | 12.917 | 13.107 | 13.090 | 13.217 | 12.909 | **13.048**  |       **3.048**        |

| RTI DDS(K3s) |   1    |   2    |   3    |   4    |   5    |   Average   | Average(no Sleep time) |
| :----------: | :----: | :----: | :----: | :----: | :----: | :---------: | :--------------------: |
|    **5K**    | 10.489 | 10.565 | 10.482 | 10.516 | 10.484 | **10.5072** |       **0.5072**       |
|   **10K**    | 10.636 | 10.496 | 10.519 | 10.521 | 10.539 | **10.5422** |       **0.5422**       |
|   **15K**    | 10.613 | 10.610 | 10.646 | 10.718 | 10.724 | **10.6622** |       **0.6622**       |
|   **20K**    | 10.715 | 10.677 | 10.652 | 10.681 | 10.611 | **10.6672** |       **0.6672**       |
|   **25K**    | 10.686 | 10.756 | 10.781 | 10.768 | 10.727 | **10.7436** |       **0.7436**       |
|   **30K**    | 10.648 | 10.636 | 10.811 | 10.801 | 10.810 | **10.7412** |       **0.7412**       |
|   **35K**    | 10.851 | 10.990 | 10.961 | 11.005 | 10.987 | **10.9588** |       **0.9588**       |
|   **40K**    | 10.877 | 10.889 | 10.808 | 11.072 | 11.084 | **10.946**  |       **0.946**        |
|   **45K**    | 10.933 | 11.077 | 11.085 | 11.098 | 11.094 | **11.0574** |       **1.0574**       |
|   **50K**    | 10.989 | 11.139 | 11.107 | 11.110 | 11.081 | **11.0852** |       **1.0852**       |

| Open DDS(K3s) |   1    |   2    |   3    |   4    |   5    |   Average   | Average(no Sleep time) |
| :-----------: | :----: | :----: | :----: | :----: | :----: | :---------: | :--------------------: |
|    **5K**     | 11.847 | 12.041 | 12.038 | 12.070 | 11.752 | **11.9496** |       **1.9496**       |
|    **10K**    | 12.040 | 12.127 | 12.322 | 12.273 | 12.243 | **12.201**  |       **2.201**        |
|    **15K**    | 12.447 | 12.066 | 12.161 | 12.317 | 12.668 | **12.3318** |       **2.3318**       |
|    **20K**    | 12.300 | 12.634 | 12.364 | 12.369 | 12.632 | **12.4598** |       **2.4598**       |
|    **25K**    | 12.802 | 12.839 | 12.278 | 12.613 | 12.434 | **12.5932** |       **2.5932**       |
|    **30K**    | 12.746 | 12.982 | 12.975 | 12.663 | 12.583 | **12.7898** |       **2.7898**       |
|    **35K**    | 13.306 | 12.902 | 13.255 | 12.967 | 13.260 | **13.138**  |       **3.138**        |
|    **40K**    | 13.271 | 13.165 | 12.938 | 13.107 | 13.142 | **13.1246** |       **3.1246**       |
|    **45K**    | 13.140 | 13.044 | 13.563 | 13.242 | 13.406 | **13.279**  |       **3.279**        |
|    **50K**    | 13.515 | 13.389 | 13.764 | 13.324 | 13.243 | **13.447**  |       **3.447**        |

| RTI DDS(K0s) |   1    |   2    |   3    |   4    |   5    |   Average   | Average(no Sleep time) |
| :----------: | :----: | :----: | :----: | :----: | :----: | :---------: | :--------------------: |
|    **5K**    | 10.451 | 10.572 | 10.529 | 10.462 | 10.545 | **10.5118** |       **0.5118**       |
|   **10K**    | 10.591 | 10.629 | 10.569 | 10.521 | 10.492 | **10.5604** |       **0.5604**       |
|   **15K**    | 10.615 | 10.642 | 10.662 | 10.639 | 10.624 | **10.6364** |       **0.6364**       |
|   **20K**    | 10.706 | 10.631 | 10.600 | 10.690 | 10.570 | **10.6394** |       **0.6394**       |
|   **25K**    | 10.763 | 10.684 | 10.619 | 10.716 | 10.741 | **10.7046** |       **0.7046**       |
|   **30K**    | 10.810 | 10.759 | 10.757 | 10.796 | 10.753 | **10.775**  |       **0.775**        |
|   **35K**    | 10.861 | 10.728 | 10.875 | 10.749 | 10.747 | **10.792**  |       **0.792**        |
|   **40K**    | 10.784 | 10.807 | 10.937 | 11.004 | 10.769 | **10.8602** |       **0.8602**       |
|   **45K**    | 11.031 | 10.868 | 11.026 | 10.929 | 10.832 | **10.9372** |       **0.9372**       |
|   **50K**    | 10.890 | 10.874 | 11.049 | 11.230 | 10.816 | **10.9718** |       **0.9718**       |

| Open DDS(K0s) |   1    |   2    |   3    |   4    |   5    |   Average   | Average(no Sleep time) |
| :-----------: | :----: | :----: | :----: | :----: | :----: | :---------: | :--------------------: |
|    **5K**     | 12.014 | 11.878 | 11.857 | 12.145 | 11.918 | **11.9624** |       **1.9624**       |
|    **10K**    | 12.023 | 12.081 | 12.169 | 12.187 | 12.236 | **12.1392** |       **2.1392**       |
|    **15K**    | 12.348 | 12.200 | 12.489 | 12.369 | 12.348 | **12.3508** |       **2.3508**       |
|    **20K**    | 12.317 | 12.689 | 12.901 | 12.580 | 12.498 | **12.597**  |       **2.597**        |
|    **25K**    | 12.486 | 12.506 | 12.609 | 12.620 | 12.737 | **12.5916** |       **2.5916**       |
|    **30K**    | 12.603 | 12.638 | 13.175 | 12.797 | 12.639 | **12.7704** |       **2.7704**       |
|    **35K**    | 13.051 | 13.039 | 13.046 | 13.082 | 13.074 | **13.0584** |       **3.0584**       |
|    **40K**    | 13.008 | 13.292 | 13.544 | 13.309 | 13.265 | **13.2836** |       **3.2836**       |
|    **45K**    | 13.192 | 13.135 | 13.496 | 13.379 | 13.377 | **13.3158** |       **3.3158**       |
|    **50K**    | 13.795 | 13.439 | 13.583 | 13.693 | 13.627 | **13.6274** |       **3.6274**       |

## Throughtput (byte/ms)
| RTI DDS(K8s) |     1     |     2     |     3     |     4     |     5     |    Average     | Average(no Sleep time) |
| :----------: | :-------: | :-------: | :-------: | :-------: | :-------: | :------------: | :--------------------: |
|    **5K**    | 455.9963  | 452.0795  | 458.0432  | 450.2476  | 455.3319  |  **454.3397**  |
|   **10K**    | 898.4725  | 912.9085  | 901.3068  | 905.3050  | 910.0837  |  **905.6153**  |
|   **15K**    | 1349.5276 | 1424.9073 | 1353.9128 | 1359.0649 | 1365.9958 | **1370.68168** |
|   **20K**    | 1805.2170 | 1809.4634 | 1787.4698 | 1782.0547 | 1845.0184 | **1805.84466** |
|   **25K**    | 2251.8465 | 2235.7360 | 2243.1583 | 2234.3372 | 2219.8543 | **2236.98646** |
|   **30K**    | 2664.0618 | 2680.2465 | 2690.3416 | 2663.3522 | 2694.2074 | **2678.4419**  |
|   **35K**    | 3102.2868 | 3021.4088 | 3121.6553 | 3098.7162 | 3137.0440 | **3096.22222** |
|   **40K**    | 3523.6081 | 3539.8230 | 3533.5689 | 3541.7035 | 3532.3207 | **3534.20484** |
|   **45K**    | 3917.1309 | 3959.5248 | 3937.6968 | 4009.2658 | 3952.9163 | **3955.30692** |
|   **50K**    | 4451.9633 | 4435.3765 | 4406.8394 | 4408.7822 | 4434.5898 | **4427.51024** |

| Open DDS(K8s) |     1     |     2     |     3     |     4     |     5     |    Average     | Average(no Sleep time) |
| :-----------: | :-------: | :-------: | :-------: | :-------: | :-------: | :------------: | :--------------------: |
|    **5K**     | 333.9790  | 334.0682  | 331.1258  | 333.0225  | 333.4000  |  **333.1191**  |
|    **10K**    | 659.6306  | 660.7638  | 657.1165  | 661.2881  | 661.3319  | **660.02618**  |
|    **15K**    | 979.4958  | 980.3280  | 974.8488  | 980.4562  | 981.3542  |  **979.2966**  |
|    **20K**    | 1297.7743 | 1284.8515 | 1298.4483 | 1296.8486 | 1297.0168 | **1294.9879**  |
|    **25K**    | 1516.7141 | 1561.5240 | 1603.0779 | 1592.1538 | 1604.3123 | **1575.55642** |
|    **30K**    | 1905.1247 | 1819.0637 | 1872.8929 | 1896.5735 | 1901.7432 | **1879.0796**  |
|    **35K**    | 2209.3170 | 2117.7467 | 2207.7840 | 2206.8095 | 2162.2289 | **2180.77722** |
|    **40K**    | 2443.7927 | 2504.0691 | 2502.9722 | 2379.9607 | 2504.8531 | **2467.12956** |
|    **45K**    | 2788.9680 | 2785.6877 | 2714.1133 | 2712.4773 | 2713.7860 | **2743.00646** |
|    **50K**    | 3079.1969 | 3005.1688 | 3001.9212 | 2863.0325 | 3088.3261 | **3007.5291**  |

| RTI DDS(K3s) |     1     |     2     |     3     |     4     |     5     |    Average     | Average(no Sleep time) |
| :----------: | :-------: | :-------: | :-------: | :-------: | :-------: | :------------: | :--------------------: |
|    **5K**    | 459.2211  | 447.4272  | 483.4944  | 447.2671  | 450.4910  | **457.58016**  |
|   **10K**    | 940.1146  | 921.9138  | 908.2652  | 934.4047  | 910.1665  | **922.97296**  |
|   **15K**    | 1373.2491 | 1356.6066 | 1363.8843 | 1351.4731 | 1354.0350 | **1359.85962** |
|   **20K**    | 1789.3889 | 1804.7283 | 1797.9144 | 1790.0295 | 1827.9864 | **1802.0095**  |
|   **25K**    | 2246.9890 | 2224.7930 | 2237.7372 | 2232.1428 | 2250.4275 | **2238.4179**  |
|   **30K**    | 2698.0843 | 2631.5789 | 2658.3961 | 2641.5426 | 2644.1036 | **2654.7411**  |
|   **35K**    | 3111.1111 | 3060.7783 | 3053.8347 | 3071.7921 | 3049.5774 | **3069.41872** |
|   **40K**    | 3519.8873 | 3517.4111 | 3522.3670 | 3476.7492 | 3485.2313 | **3504.32918** |
|   **45K**    | 3945.2919 | 3897.1161 | 3886.0103 | 3904.5553 | 3906.9282 | **3907.98036** |
|   **50K**    | 4454.3429 | 4305.8904 | 4378.6671 | 4332.3802 | 4343.6712 | **4362.99036** |

| Open DDS(K3s) |     1     |     2     |     3     |     4     |     5     |    Average     | Average(no Sleep time) |
| :-----------: | :-------: | :-------: | :-------: | :-------: | :-------: | :------------: | :--------------------: |
|    **5K**     | 331.4770  | 327.4823  | 327.1823  | 324.7385  | 332.0494  |  **328.5859**  |
|    **10K**    | 654.7502  | 602.0469  | 639.3861  | 642.2195  | 646.2035  | **636.92124**  |
|    **15K**    | 957.8544  | 977.5171  | 898.0960  | 913.1864  | 857.7800  | **920.88678**  |
|    **20K**    | 1286.6700 | 1260.1600 | 1248.6732 | 1281.3941 | 1259.4458 | **1267.26862** |
|    **25K**    | 1558.5063 | 1555.3067 | 1609.6838 | 1521.1439 | 1591.1405 | **1567.15624** |
|    **30K**    | 1877.3466 | 1848.5427 | 1849.7965 | 1887.6234 | 1894.0589 | **1871.47362** |
|    **35K**    | 2110.8497 | 2167.7195 | 2122.2410 | 2162.3625 | 2005.8456 | **2113.80366** |
|    **40K**    | 2293.9725 | 2384.3586 | 2471.8823 | 2450.9803 | 2308.6690 | **2381.97254** |
|    **45K**    | 2526.2448 | 2753.3039 | 2481.3895 | 2375.1715 | 2565.7107 | **2540.36408** |
|    **50K**    | 2834.1457 | 2912.0559 | 2566.8668 | 3002.6423 | 3009.3289 | **2865.00792** |

| RTI DDS(K0s) |     1     |     2     |     3     |     4     |     5     |    Average     | Average(no Sleep time) |
| :----------: | :-------: | :-------: | :-------: | :-------: | :-------: | :------------: | :--------------------: |
|    **5K**    | 452.3658  | 449.7211  | 451.6711  | 452.2840  | 453.1037  | **451.82914**  |
|   **10K**    | 903.1791  | 876.1170  | 911.6601  | 900.2520  | 905.4690  | **899.33544**  |
|   **15K**    | 1311.6474 | 1340.2430 | 1340.9619 | 1374.5070 | 1336.5410 | **1340.78006** |
|   **20K**    | 1772.5782 | 1782.6900 | 1812.5793 | 1793.4002 | 1795.8157 | **1791.41268** |
|   **25K**    | 2224.0014 | 2220.6430 | 2237.9375 | 2219.0662 | 2229.5549 | **2226.2406**  |
|   **30K**    | 2637.3626 | 2636.8990 | 2642.0079 | 2644.8029 | 2673.7967 | **2646.97382** |
|   **35K**    | 3050.6406 | 3098.1676 | 3062.6531 | 3096.7970 | 3078.8177 | **3077.4152**  |
|   **40K**    | 3525.1608 | 3546.4136 | 3490.0968 | 3445.6025 | 3556.8202 | **3512.81878** |
|   **45K**    | 3632.2544 | 3944.2545 | 3891.3870 | 3939.4204 | 3932.5351 | **3867.97028** |
|   **50K**    | 4418.9129 | 4377.1338 | 4297.3785 | 4258.9437 | 4418.1320 | **4354.10198** |

| Open DDS(K0s) |     1     |     2     |     3     |     4     |     5     |    Average     | Average(no Sleep time) |
| :-----------: | :-------: | :-------: | :-------: | :-------: | :-------: | :------------: | :--------------------: |
|    **5K**     | 326.4986  | 329.4675  | 328.9906  | 323.6874  | 328.2563  | **327.38008**  |
|    **10K**    | 652.7415  | 650.1527  | 645.2861  | 645.0780  | 643.5420  | **647.36006**  |
|    **15K**    | 958.2215  | 964.3821  | 949.5473  | 956.6326  | 958.0991  | **957.37652**  |
|    **20K**    | 1279.7542 | 1247.2715 | 1234.4916 | 1206.4909 | 1265.6625 | **1246.73414** |
|    **25K**    | 1577.3865 | 1580.9776 | 1566.5141 | 1569.2674 | 1557.5353 | **1570.33618** |
|    **30K**    | 1885.9621 | 1876.4073 | 1816.4204 | 1810.7194 | 1879.3459 | **1853.77102** |
|    **35K**    | 2140.0183 | 2140.6727 | 2139.6258 | 2129.4718 | 2133.3658 | **2136.63088** |
|    **40K**    | 2380.6689 | 2407.7529 | 2368.4054 | 2398.9444 | 2411.8179 | **2393.5179**  |
|    **45K**    | 2716.0791 | 2726.6117 | 2566.5887 | 2564.8332 | 2529.3687 | **2620.69628** |
|    **50K**    | 2749.5188 | 2807.5692 | 2816.9014 | 2771.7722 | 2774.6947 | **2784.09126** |