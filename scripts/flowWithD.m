function flowWithD(diameter, dirName)
    fid = fopen(sprintf("./output/flow_file_D=%d.txt", diameter));

	# Read initial out time
    	initialT = str2num(fgetl(fid));
    	times = [initialT];

        # Read file
    	while (!feof(fid))
    	    # Parse out time
    	    times = [times, str2num(fgetl(fid))];
    	endwhile

    	fclose(fid);

    	N = 25;

    	lowerLimit = 1;
    	finalLowerLimit = size(times,2) - N;

    	numberOfFlows = finalLowerLimit;

    	flows = zeros(numberOfFlows, 1);

    	deltaTs = zeros(numberOfFlows, 1);

    	for i = 1:1:finalLowerLimit
    		ti = times(i);
    		tf = times(i + N);
    		deltaT = tf - ti;
    		flows(i) = N / deltaT;
    		deltaTs(i) = times(i + N);
    	endfor

        props = {"marker", '.', 'LineStyle', 'none'};
        h = plot(deltaTs, flows);
        set(h, props{:})
        xlabel("Tiempo (s)");
        ylabel("Caudal [part./s]");
        axis([0 round(times(end))])
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

        print(sprintf("%s/flow-N=%d-T=%ds-D=%d.png", dirName, N, round(times(end)), diameter), "-dpngcairo", "-F:14")
end

