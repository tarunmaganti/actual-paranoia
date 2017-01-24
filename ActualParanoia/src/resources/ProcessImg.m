#!/usr/bin/octave -qf
function ret = manipulateCell(x)

	x = x/max(x(:));
	x = x.*255;
	x = int32(x);
	a1 = mean(x(:));

	ret = a1<70;
end

img = imread('src/resources/logo/aaa.png');
imgInd = rgb2ind(img);
imgGray = ind2gray(imgInd,colormap());

sizeVector = 100*ones(1,20);


Cells =  mat2cell(imgGray,sizeVector,sizeVector);

ManipCells = cellfun(@manipulateCell,Cells);

file2D = fopen('src/resources/data.txt','w+');
dlmwrite(file2D,ManipCells);



