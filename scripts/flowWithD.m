function flowWithD(diameter, dirName)
    fid = fopen(sprintf("./output/flow_file_D=%d.txt", diameter));

	    # Read initial out time
	    times = [0.0];
    	initialT = str2num(fgetl(fid));
    	times = [times, initialT];

        # Read file
        lineCounter = 1;
    	while (!feof(fid))
    	    # Parse out time
    	    times = [times, str2num(fgetl(fid))];
    	endwhile

    	fclose(fid);

    	N = 50;

    	lowerLimit = 100;
    	finalLowerLimit = size(times,2) - N;

    	numberOfFlows = finalLowerLimit;

    	flows = zeros(numberOfFlows + 1, 1);
    	flows(1) = 0.0;

    	deltaTs = zeros(numberOfFlows + 1, 1);
        deltaTs(1) = 0.0;

    	for i = lowerLimit:1:finalLowerLimit
    		ti = times(i);
    		tf = times(i + N);
    		deltaT = tf - ti;
    		flows(i+1) = N / deltaT;
    		deltaTs(i+1) = times(i + N);
    	endfor

        props = {"marker", '.', 'LineStyle', 'none'};
        h = plot(deltaTs, flows, sprintf(";D = %dm;", diameter));
        set(h, props{:})
        xlabel("Tiempo (s)");
        ylabel("Caudal [part./s]");
        legend("location", "eastoutside");
        axis([0 round(times(end))])
        xlim([0, 16])
        grid on

        m = mean(flows);
        means_file_id = fopen('./scripts/means.txt', 'a');
        fprintf(means_file_id, '%e ', m);
        fclose(means_file_id);

        s = std(flows);
        stds_file_id = fopen('./scripts/stds.txt', 'a');
        fprintf(stds_file_id, '%e ', s);
        fclose(stds_file_id);

        hold all

        print(sprintf("%s/flow-N=%d-T=%ds-D=%d.png", dirName, N, round(times(end)), diameter), "-dpngcairo", "-F:12")
end

