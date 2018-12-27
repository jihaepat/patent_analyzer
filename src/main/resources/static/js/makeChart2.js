
//var backgrounds = ["#4170c7","#ef7d22","#a5a5a5","#ffc100","#589ad7","#6eae40","#23437a","#a04800","#636363","#9a7400","#215d93","#426927","#678cd2","#f39753","#b7b7b7","#ffce0b","#7aaedf","#8ac263","#3158a3","#d46000","#848484","#cd9b00","#2e7bc4","#598b34","#8ea9de","#f4b17f","#c9c9c9","#ffda5c","#9cc2e6","#a8d28b","#1f3765"];
var backgroundColors = ["rgba(65, 112, 199, 0.5)","rgba(239, 125, 34, 0.5)","rgba(165, 165, 165, 0.5)","rgba(255, 193, 0, 0.5)","rgba(88, 154, 215, 0.5)"
	,"rgba(110, 174, 64, 0.5)","rgba(35, 67, 122, 0.5)","rgba(160, 72, 0, 0.5)","rgba(99, 99, 99, 0.5)","rgba(154, 116, 0, 0.5)","rgba(33, 93, 147, 0.5)"
	,"rgba(66, 105, 39, 0.5)","rgba(103, 140, 210, 0.5)","rgba(243, 151, 83, 0.5)","rgba(183, 183, 183, 0.5)","rgba(255, 206, 11, 0.5)","rgba(122, 174, 223, 0.5)"
	,"rgba(138, 194, 99, 0.5)","rgba(49, 88, 163, 0.5)","rgba(212, 96, 0, 0.5)","rgba(132, 132, 132, 0.5)","rgba(205, 155, 0, 0.5)","rgba(46, 123, 196, 0.5)"
	,"rgba(89, 139, 52, 0.5)","rgba(142, 169, 222, 0.5)","rgba(244, 177, 127, 0.5)","rgba(201, 201, 201, 0.5)","rgba(255, 218, 92, 0.5)","rgba(156, 194, 230, 0.5)"
	,"rgba(168, 210, 139, 0.5)","rgba(31, 55, 101, 0.5)"];

function fncChartOpt(id, obj, title) {
	if (!title) {title = '';}
	
	var opt = {
			legend: { 
				display: true ,
				position: "bottom"
			},
			scales: {
				xAxes: [],
				yAxes: [{
					ticks: {
						beginAtZero: true
					}
				}]
			},
			title: {
				display: true,
		        text: title
			},
			tooltips: {
		         enabled: false
		    },
			animation: {
				duration: 0, // general animation time
				onComplete : function(){
					var ctx = this.chart.ctx;
					
			        ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize+8, 'normal', Chart.defaults.global.defaultFontFamily);
			        ctx.fillStyle = "black";
			        ctx.textAlign = 'center';
			        ctx.textBaseline = 'bottom';

			        this.data.datasets.forEach(function (dataset) {
			        	var type = dataset.type;
			            for (var i = 0; i < dataset.data.length; i++) {
			                for(var key in dataset._meta) {
			                    var model = dataset._meta[key].data[i]._model;
			                    
			                    if (dataset.data[i] != 0) {
			                    	ctx.fillStyle=model.backgroundColor.replace(/[\d\.]+\)$/g, '1)');
			                    }
			                    if (type == 'line') {
			                    	if (dataset.data[i] > 0) {
				                    	ctx.fillText(dataset.data[i], model.x+10, model.y-5);
				                    } else if (dataset.data[i] < 0) {
				                    	ctx.fillText(dataset.data[i], model.x+10, model.y+25);
				                    }
			                    } else {
			                    	if (dataset.data[i] > 0) {
				                    	ctx.fillText(dataset.data[i], model.x, model.y+30);
				                    } else if (dataset.data[i] < 0) {
				                    	ctx.fillText(dataset.data[i], model.x, model.y-30);
				                    }
			                    }
			                }
			            }
			        });

			        if (document.getElementById(id)) {
			        	obj.rt = document.getElementById(id).toDataURL();	
			        }
				}
			}
		}; 
	
	return opt;
}

function fncChartStackOpt(id, obj, title) {
	if (!title) {title = '';}

	var opt = {
			legend: { 
				display: true ,
				position: "bottom"
			},
			title: {
				display: true,
		        text: title
			},
			tooltips: {
		         enabled: false
		    },
			scales: {
				xAxes: [
					{ 
						stacked: true,
						ticks: {
							beginAtZero: true
						}
					}
				],
				yAxes: [{ stacked: true }]
			},
			animation: {
				duration: 0, // general animation time
				onComplete : function(){
					var ctx = this.chart.ctx;
			        ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize+8, 'normal', Chart.defaults.global.defaultFontFamily);
			        ctx.fillStyle = "black";
			        ctx.textAlign = 'center';
			        ctx.textBaseline = 'bottom';

			        this.data.datasets.forEach(function (dataset) {
			            for (var i = 0; i < dataset.data.length; i++) {
			                for(var key in dataset._meta) {
			                    var model = dataset._meta[key].data[i]._model;
			                    
			                    ctx.fillStyle=model.backgroundColor.replace(/[\d\.]+\)$/g, '1)');
			                    
		                    	if (dataset.data[i] > 0) {
			                    	ctx.fillText(dataset.data[i], model.x, model.y+30);
			                    } else if (dataset.data[i] < 0) {
			                    	ctx.fillText(dataset.data[i], model.x, model.y-20);
			                    }
			                    	
			                }
			            }
			        });

			        if (document.getElementById(id)) {
			        	obj.rt = document.getElementById(id).toDataURL();	
			        }
				}
			}
		}; 
	if (title) {
		opt.title.display = true;
		opt.title.text = title;
	}
	return opt;
}

function makeCanvas(pctx, pId, sbId) {
	var cId = pId + sbId;
	pctx.append("<canvas id='"+cId+"'></canvas>");
	return cId;
}

var randomColorGenerator = function (i) { 
	if (backgroundColors.length>i) {
		return backgroundColors[i];
	} else {
		var rint = Math.floor( 0x100000000 * Math.random());
		return 'rgba(' + (rint & 255) + ',' + (rint >> 8 & 255) + ',' + (rint >> 16 & 255) + ', 0.5)';	
	}
};

function getChartInfo(pId, sbId, type, inurl) {
	var obj = {rt: null};
	$.ajax({
		url:inurl,
		async: false,
		type: 'POST',
		dataType:'json',
		success: function(data) {
			if (data.result) {
				var pctx = $('#'+pId);
				if (data.info) {
					if (type == 'stack') {
						stackBarChart(makeCanvas(pctx, pId, sbId), data.info, obj);
					} else if (type == 'area') {
						areaChart(makeCanvas(pctx, pId, sbId), data.info, obj);
					} else if (type == 'pie') {
						pieChart(makeCanvas(pctx, pId, sbId), data.info, obj);
					} else if (type == 'bubble') {
						bubbleChart(makeCanvas(pctx, pId, sbId), data.info, obj);
					} else if (type == 'horizontal') {
						horizontalBarChart(makeCanvas(pctx, pId, sbId), data.info, obj);
					} else if (type == 'horizontalStack') {
						horizontalStackBarChart(makeCanvas(pctx, pId, sbId), data.info, obj);
					}
				} else if (data.infos) {
					if (type == 'mix') {
						$.each(data.infos, function (key, value) {
							mixedChart(makeCanvas(pctx, pId, sbId+''+key), value, obj);
						});
					} else if (type == 'mix2') {
						$.each(data.infos, function (key, value) {
							mixedChart2(makeCanvas(pctx, pId, sbId+''+key), value, obj);
						});
					} else if (type == 'pie') {
						$.each(data.infos, function (key, value) {
							pieChart(makeCanvas(pctx, pId, sbId+''+key), value, obj);
						});
					} else if (type == 'bubble') {
						$.each(data.infos, function (key, value) {
							bubbleChart(makeCanvas(pctx, pId, sbId+''+key), value, obj);
						});
					} else if (type == 'horizontal') {
						$.each(data.infos, function (key, value) {
							horizontalBarChart(makeCanvas(pctx, pId, sbId+''+key), value, obj);
						});
					}
				}
			}
		}
	});
	
	return obj.rt;
}

function horizontalBarChart(id, data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	var dataset = {};
	
	dataset.label = '';
	dataset.backgroundColor = backgroundColors;
	dataset.data = data.dataSet;
	
	datasets.push(dataset);
	
	var ctx = $('#'+id);
	ctx.innerHTML = "";
	
	var opt = fncChartOpt(id, obj, data.name);
	opt.scales.xAxes.push({ticks:{min: 0}});
	opt.legend.display = false;

	new Chart(ctx, {
		type: 'horizontalBar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: {
			legend: { 
				display: false ,
				position: "bottom"
			},
			scales: {
				xAxes: [{ticks:{min: 0}}],
				yAxes: [{ ticks: { beginAtZero: true } }]
			},
			title: {
				display: true,
		        text: data.name
			},
			tooltips: {
		         enabled: false
		    },
			animation: {
				duration: 0, // general animation time
				onComplete : function(){
					var ctx = this.chart.ctx;
					
			        ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize+8, 'normal', Chart.defaults.global.defaultFontFamily);
			        ctx.fillStyle = "black";
			        ctx.textAlign = 'center';
			        ctx.textBaseline = 'bottom';

			        this.data.datasets.forEach(function (dataset) {
			            for (var i = 0; i < dataset.data.length; i++) {
			                for(var key in dataset._meta) {
			                    var model = dataset._meta[key].data[i]._model;
			                    
			                    if (dataset.data[i] != 0) {
			                    	ctx.fillStyle=model.backgroundColor.replace(/[\d\.]+\)$/g, '1)');
			                    	ctx.fillText(dataset.data[i], model.x-20, model.y+10);
			                    }
			                }
			            }
			        });

			        if (document.getElementById(id)) {
			        	obj.rt = document.getElementById(id).toDataURL();	
			        }
				}
			}
		}
	});
}

function horizontalStackBarChart(id, data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	$.each(data.dataSet, function (key, value) {
		var dataset = {};
		
		dataset.label = key;
		dataset.backgroundColor = randomColorGenerator(i++);
		dataset.data = value;
		
		datasets.push(dataset);
	});
	
	var ctx = $('#'+id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'horizontalBar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: {
			legend: { 
				display: true ,
				position: "bottom"
			},
			title: {
				display: true,
				text: data.name
			},
			tooltips: {
		         enabled: false
		    },
			scales: {
				xAxes: [
					{ 
						stacked: true,
						ticks: {
							beginAtZero: true
						}
					}
				],
				yAxes: [{ stacked: true }]
			},
			animation: {
				duration: 0, // general animation time
				onComplete : function(){
					var ctx = this.chart.ctx;
			        ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize+8, 'normal', Chart.defaults.global.defaultFontFamily);
			        ctx.fillStyle = "black";
			        ctx.textAlign = 'center';
			        ctx.textBaseline = 'bottom';

			        this.data.datasets.forEach(function (dataset) {
			            for (var i = 0; i < dataset.data.length; i++) {
			                for(var key in dataset._meta) {
			                    var model = dataset._meta[key].data[i]._model;
			                    
			                    if (dataset.data[i] != 0) {
			                    	ctx.fillStyle=model.backgroundColor.replace(/[\d\.]+\)$/g, '1)');
			                    	ctx.fillText(dataset.data[i], model.x-20, model.y+10);
			                    }
			                }
			            }
			        });

			        if (document.getElementById(id)) {
			        	obj.rt = document.getElementById(id).toDataURL();	
			        }
				}
			}
		}
	});
}

function stackBarChart(id, data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	$.each(data.dataSet, function (key, value) {
		var dataset = {};
		
		dataset.label = key;
		dataset.backgroundColor = randomColorGenerator(i++);
		dataset.data = value;
		
		datasets.push(dataset);
	});
	
	var ctx = $('#'+id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'bar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: fncChartStackOpt(id, obj, data.name)
	});
}

function areaChart(id, data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	$.each(data.dataSet, function (key, value) {
		var dataset = {};
		
		dataset.label = key;
		dataset.fill = true,
		dataset.backgroundColor = randomColorGenerator(i++);
		dataset.data = value;
		
		datasets.push(dataset);
	});
	
	var ctx = $('#'+id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'line',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: fncChartStackOpt(id, obj, data.name)
	});
}

function pieChart(id, data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	var dataset = {};
	
	dataset.label = '';
	dataset.backgroundColor = backgroundColors;
	dataset.data = data.dataSet;
	
	datasets.push(dataset);
	
	var ctx = $('#'+id);
	ctx.innerHTML = "";
	
	var title = "";
	if (data.name) title = data.name;
	new Chart(ctx, {
		type: 'pie',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: {
			legend: { 
				display: true ,
				position: "bottom"
			},
			title: {
				display: true,
		        text: title
			},
			tooltips: {
		         enabled: false
		    },
			animation: {
				duration: 0, // general animation time
				onComplete : function(){
//					console.log(" animation onComplete ");
					
					var ctx = this.chart.ctx;
					ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize+8, 'normal', Chart.defaults.global.defaultFontFamily);
					ctx.textAlign = 'center';
					ctx.textBaseline = 'bottom';

					this.data.datasets.forEach(function (dataset) {
						for (var i = 0; i < dataset.data.length; i++) {
							var model = dataset._meta[Object.keys(dataset._meta)[0]].data[i]._model,
							total = dataset._meta[Object.keys(dataset._meta)[0]].total,
							mid_radius = model.innerRadius + (model.outerRadius - model.innerRadius)/2,
							start_angle = model.startAngle,
							end_angle = model.endAngle,
							mid_angle = start_angle + (end_angle - start_angle)/2;

							var x = mid_radius * Math.cos(mid_angle);
							var y = mid_radius * Math.sin(mid_angle);

							ctx.fillStyle = '#444';
							var percent = String(Math.round(dataset.data[i]/total*100)) + "%";
							ctx.fillText(dataset.data[i], model.x + x, model.y + y);
							ctx.fillText(percent, model.x + x, model.y + y + 15);
						}
					});
					
			        if (document.getElementById(id)) {
			        	obj.rt = document.getElementById(id).toDataURL();	
			        }
				}
			}
		}
	});
}

function mixedChart(id, data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	$.each(data.dataSet, function (key, v) {
		var datasetBar = {};
		datasetBar.label = key+'건수';
		datasetBar.type = "bar";
		datasetBar.backgroundColor = randomColorGenerator(i++);
		datasetBar.data = v.v1;
		
		var lineColor = randomColorGenerator(i++);
		var datasetLine = {};
		datasetLine.label = key+'증감율';
		datasetLine.type = "line";
		datasetLine.backgroundColor = lineColor;
		datasetLine.borderColor = lineColor;
		datasetLine.data = v.v2;
		datasetLine.fill = false;
		
		datasets.push(datasetBar);
		datasets.push(datasetLine);
	});
	
	var ctx = $('#'+id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'bar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: fncChartOpt(id, obj, data.name)
	});
}

function mixedChart2(id, data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	$.each(data.dataSet, function (key, value) {
		$.each(value, function (nat, v) {
			var datasetBar = {};
			datasetBar.label = nat+'건수';
			datasetBar.type = "bar";
			datasetBar.backgroundColor = randomColorGenerator(i++);
			datasetBar.data = v.v1;
			
			var lineColor = randomColorGenerator(i++);
			var datasetLine = {};
			datasetLine.label = nat+'증감율';
			datasetLine.type = "line";
			datasetLine.backgroundColor = lineColor;
			datasetLine.borderColor = lineColor;
			datasetLine.data = v.v2;
			datasetLine.fill = false;
			
			datasets.push(datasetBar);
			datasets.push(datasetLine);
		});
	});
	
	var ctx = $('#'+id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'bar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: fncChartOpt(id, obj, data.name)
	});
}

function bubbleChart(id, data, obj) {
	var datasets = []; var i=0;
	$.each(data.dataSet, function (key, value) {
		var dataset = {};
		
		dataset.label = key;
		dataset.backgroundColor = randomColorGenerator(i++);
		dataset.data = [{x: value.v1*1, y: value.v2*1, value: value.v1*value.v2}];
		
		datasets.push(dataset);
	});
	
	var ctx = $('#'+id);
	ctx.innerHTML = "";
	
	var title = "";
	if (data.name) title = data.name;
	
	new Chart(ctx, {
		type: 'bubble',
		responsive: false,
		data: {
			datasets: datasets
		}, 
		options: {
			legend: { 
				display: true ,
				position: "bottom"
			},
			title: {
				display: true,
				text: title
			},
			tooltips: {
		         enabled: false
		    },
			animation: {
				duration: 0, // general animation time
				onComplete : function(){
					var ctx = this.chart.ctx;
			        ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize+8, 'normal', Chart.defaults.global.defaultFontFamily);
			        ctx.fillStyle = "black";
			        ctx.textAlign = 'center';
			        ctx.textBaseline = 'bottom';

			        this.data.datasets.forEach(function (dataset) {
			            for (var i = 0; i < dataset.data.length; i++) {
			                for(var key in dataset._meta) {
			                    var model = dataset._meta[key].data[i]._model;
			                    var txt = 'X:'+dataset.data[i].x +', Y:'+ dataset.data[i].y;
		                    	ctx.fillText(txt, model.x, model.y+10);
			                }
			            }
			        });

			        if (document.getElementById(id)) {
			        	obj.rt = document.getElementById(id).toDataURL();	
			        }
				}
			},
			elements: {
				point: {
					radius: function(context) {
						var index = context.dataIndex;
						var data = context.dataset.data[index];
						var size = context.chart.width;
						var base = data.value;
						return (size / 24) * base;
					}
				}
			}
		}
	});
}

























































