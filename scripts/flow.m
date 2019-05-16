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

	numberOfFlows = floor(size(times,2) / N);

	flows = zeros(numberOfFlows, 1);

	deltaTs = zeros(numberOfFlows, 1);

	j = i + N;

	for j = (1 + N):(N + 1):(numberOfFlows * (N + 1))
		i = j - N;
		ti = times(i);
		tf = times(j);
		deltaT = tf - ti;
		index = j / (1 + N);
		flows(index) = N / deltaT;
		deltaTs(index) = deltaT;
	endfor

    props = {"marker", '.', 'LineStyle', 'none'};
    h = plot(deltaTs, flows);
    set(h, props{:})
    xlabel("Tiempo (s)");
    ylabel("Caudal [part./s]");
    % set(gca, "yticklabel", num2str(get(gca, "ytick"), '%.e|'))
    axis([0 times(end)])
    grid on

	print(sprintf("./output/flow-T=%ds.png", round(times(end))), "-dpngcairo", "-F:14")
end

