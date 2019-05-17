function flow
	fid = fopen('./output/flow_file_D=0.15.txt');

	# Read initial out time
	initialT = str2num(fgetl(fid));
	times = [initialT];

    # Read file
	while (!feof(fid))
	    # Parse out time
	    times = [times, str2num(fgetl(fid))];
	endwhile

	fclose(fid);

	N = 50;

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

	print(sprintf("./output/flow-T=%ds.png", round(times(end))), "-dpngcairo", "-F:14")
end

