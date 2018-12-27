
//var backgrounds = ["#4170c7","#ef7d22","#a5a5a5","#ffc100","#589ad7","#6eae40","#23437a","#a04800","#636363","#9a7400","#215d93","#426927","#678cd2","#f39753","#b7b7b7","#ffce0b","#7aaedf","#8ac263","#3158a3","#d46000","#848484","#cd9b00","#2e7bc4","#598b34","#8ea9de","#f4b17f","#c9c9c9","#ffda5c","#9cc2e6","#a8d28b","#1f3765"];
var backgroundColors = ["rgba(65, 112, 199, 0.5)","rgba(239, 125, 34, 0.5)","rgba(165, 165, 165, 0.5)","rgba(255, 193, 0, 0.5)","rgba(88, 154, 215, 0.5)"
	,"rgba(110, 174, 64, 0.5)","rgba(35, 67, 122, 0.5)","rgba(160, 72, 0, 0.5)","rgba(99, 99, 99, 0.5)","rgba(154, 116, 0, 0.5)","rgba(33, 93, 147, 0.5)"
	,"rgba(66, 105, 39, 0.5)","rgba(103, 140, 210, 0.5)","rgba(243, 151, 83, 0.5)","rgba(183, 183, 183, 0.5)","rgba(255, 206, 11, 0.5)","rgba(122, 174, 223, 0.5)"
	,"rgba(138, 194, 99, 0.5)","rgba(49, 88, 163, 0.5)","rgba(212, 96, 0, 0.5)","rgba(132, 132, 132, 0.5)","rgba(205, 155, 0, 0.5)","rgba(46, 123, 196, 0.5)"
	,"rgba(89, 139, 52, 0.5)","rgba(142, 169, 222, 0.5)","rgba(244, 177, 127, 0.5)","rgba(201, 201, 201, 0.5)","rgba(255, 218, 92, 0.5)","rgba(156, 194, 230, 0.5)"
	,"rgba(168, 210, 139, 0.5)","rgba(31, 55, 101, 0.5)"];

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

String.prototype.format = String.prototype.format || function () {
    "use strict";
    var str = this.toString();
    if (arguments.length) {
        var t = typeof arguments[0];
        var key, args = ("string" === t || "number" === t) ? Array.prototype.slice.call(arguments) : arguments[0];

        for (key in args) {
            str = str.replace(new RegExp("\\{" + key + "\\}", "gi"), args[key]);
        }
    }

    return str;
};

var chtOpt = function (opt, obj, hide) {
	var setting = $.extend({}, 
		{
			legend: {display: true, position: "bottom"},
			scales: {xAxes: [], yAxes: []},
			title: {display: false},
			tooltips: {enabled: false},
			animation: {
				duration: 0,
				onComplete : function() {
					if (obj) {
						if (!obj.rt && document.getElementById(obj.id)) {
				        	obj.rt = document.getElementById(obj.id).toDataURL();	
				        }
					}
				}
			}
		}, opt);
	
	if (!hide) {
		setting.plugins.datalabels = $.extend({}, 
		{
			align: "end",
			anchor : "end",
			color: "rgba(0, 0, 0, 0.7)",
//					backgroundColor: "rgba(0, 0, 0, 0.5)",
			borderRadius: 4,
			font: { weight: 'bold' },
			formatter: function(v, c) {
				if (v != 0)	return v;
				else		return null;
			}
		}, opt.plugins.datalabels);
	}

	return setting;
}

function getChartInfo(pId, sbId, type, inurl) {
	var objs = [];
	
	$.ajax({
		url:inurl,
		async: false,
		type: 'POST',
		dataType:'json',
		success: function(data) {
			if (data.result) {
				var pctx = $('#'+pId);
				if (data.info) {
					var obj={rt: null,id:-1};
					obj.id=makeCanvas(pctx, pId, sbId);
					objs.push(obj);
					
					if (type == 'stack') {
						stackBarChart(data.info, obj);
					} else if (type == 'area') {
						areaChart(data.info, obj);
					} else if (type == 'pie') {
						pieChart(data.info, obj);
					} else if (type == 'bubble') {
						bubbleChart(data.info, obj);
					} else if (type == 'horizontal') {
						horizontalBarChart(data.info, obj);
					} else if (type == 'horizontalStack') {
						horizontalStackBarChart(data.info, obj);
					}
				} else if (data.infos) {
					if (type == 'mix') {
						$.each(data.infos, function (key, value) {
							var obj={rt: null,id:-1};
							obj.id=makeCanvas(pctx, pId, sbId+''+key);
							objs.push(obj);
							
							mixedChart(value, obj);
						});
					} else if (type == 'mix2') {
						$.each(data.infos, function (key, value) {
							var obj={rt: null,id:-1};
							obj.id=makeCanvas(pctx, pId, sbId+''+key);
							objs.push(obj);
							
							mixedChart2(value, obj);
						});
					} else if (type == 'pie') {
						$.each(data.infos, function (key, value) {
							var obj={rt: null,id:-1};
							obj.id=makeCanvas(pctx, pId, sbId+''+key);
							objs.push(obj);
							
							pieChart(value, obj);
						});
					} else if (type == 'bubble') {
						$.each(data.infos, function (key, value) {
							var obj={rt: null,id:-1};
							obj.id=makeCanvas(pctx, pId, sbId+''+key);
							objs.push(obj);
							
							bubbleChart(value, obj);
						});
					} else if (type == 'horizontal') {
						$.each(data.infos, function (key, value) {
							var obj={rt: null,id:-1};
							obj.id=makeCanvas(pctx, pId, sbId+''+key);
							objs.push(obj);
							
							horizontalBarChart(value, obj);
						});
					}
				}
			}
		}
	});
	
	return objs;
}

function horizontalBarChart(data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	var dataset = {};
	
	dataset.label = '';
	dataset.backgroundColor = backgroundColors;
	dataset.data = data.dataSet;
	
	datasets.push(dataset);
	
	var ctx = $('#'+obj.id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'horizontalBar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: 	chtOpt({
						title: {display: true, text: data.name},
						legend: {display: false},
						scales: {xAxes: [{ticks:{beginAtZero:true}}], yAxes: [{ticks:{beginAtZero:true}}]},
						plugins: {
							datalabels: {
								align : function(context) {
									var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "end";
				        			}
				        			return "start";
					        	},
					        	anchor : function(context) {
					        		var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "start";
				        			}
				        			return "end";
					        	}
							}
						}
					}, obj)
	});
}

function horizontalStackBarChart(data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	$.each(data.dataSet, function (key, value) {
		var dataset = {};
		
		dataset.label = key;
		dataset.backgroundColor = randomColorGenerator(i++);
		dataset.data = value;
		
		datasets.push(dataset);
	});
	
	var ctx = $('#'+obj.id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'horizontalBar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: 	chtOpt({
						title: {display: true, text: data.name},
						legend: {display: false},
						scales: {xAxes: [{stacked:true,ticks:{beginAtZero:true}}], yAxes: [{stacked:true}]},
						plugins: {
							datalabels: {
								align : function(context) {
									var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "end";
				        			}
				        			return "start";
					        	},
					        	anchor : function(context) {
					        		var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "start";
				        			}
				        			return "end";
					        	}
							}
						}
					}, obj)
	});
}

function stackBarChart(data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	$.each(data.dataSet, function (key, value) {
		var dataset = {};
		
		dataset.label = key;
		dataset.backgroundColor = randomColorGenerator(i++);
		dataset.data = value;
		
		datasets.push(dataset);
	});
	
	var ctx = $('#'+obj.id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'bar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: 	chtOpt({
						title: {display: true, text: data.name},
						scales: {xAxes: [{stacked:true,ticks:{beginAtZero:true}}], yAxes: [{stacked:true}]},
						plugins: {
							datalabels: {
								align : function (context) {
									var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "end";
				        			}
				        			return "start";
					        	},
					        	anchor : function (context) {
					        		var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "start";
				        			}
				        			return "end";
					        	}
							}
						}
					}, obj)
	});
}

function areaChart(data, obj) {
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
	
	var ctx = $('#'+obj.id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'line',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: 	chtOpt({
						title: {display: true, text: data.name},
						plugins: {
							datalabels: {
								align : function(context) {
									var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "end";
				        			}
				        			return "start";
					        	},
					        	anchor : function(context) {
					        		var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "start";
				        			}
				        			return "end";
					        	}
							}
						}
					}, obj)
	});
}

function pieChart(data, obj) {
	var label = data.label;
	var datasets = []; var i=0;
	var dataset = {};
	
	dataset.label = '';
	dataset.backgroundColor = backgroundColors;
	dataset.data = data.dataSet;
	
	datasets.push(dataset);
	
	var ctx = $('#'+obj.id);
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
		options: 	chtOpt({
						title: {display: true, text: data.name},
						layout: {padding: {left:50, right:50, top:50, bottom:50}},
						segmentShowStroke: false,
						legend: { 
							display: false, 
							position: 'bottom',
							labels: {
								generateLabels: function(chart) {
									var data = chart.data;
									if (data.labels.length && data.datasets.length) {
										return data.labels.map(function(label, i) {
											var meta = chart.getDatasetMeta(0);
											var ds = data.datasets[0];
											var rt = calcPiePercent2(ds.data, i);
											
											var arc = meta.data[i];
											var custom = arc && arc.custom || {};
											var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
											var arcOpts = chart.options.elements.arc;
											var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
											var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
											var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);
											
											return {
												text: "{0} ({1}건, {2}%)".format(label,rt.v,rt.p),
												fillStyle: fill,
												strokeStyle: stroke,
												lineWidth: bw,
												hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
												index: i
							              	};
										});
									}
									
									return [];
								}
							}
						},
						plugins: {
							datalabels: {
								align : function(context) {
									if (calcPiePercent(context).p < 20) {
										if (context.dataIndex % 2) {
											return "center";
										}
										return "center";	
									}
					        		return "start";
					        	},
					        	anchor : function(context) {
					        		if (calcPiePercent(context).p < 20) {
										if (context.dataIndex % 2) {
											return "end";
										}
										return "center";	
									}
					        		return "end";
					        	},
//								backgroundColor: function(context) {
//									if (context.dataset.borderColor) {
//										return context.dataset.borderColor.replace(/[\d\.]+\)$/g, '0.5)');
//									} else if (context.dataset.backgroundColor) {
//										if (Array.isArray(context.dataset.backgroundColor)) {
//											return context.dataset.backgroundColor[context.dataIndex].replace(/[\d\.]+\)$/g, '0.5)');
//										}
//										return context.dataset.backgroundColor.replace(/[\d\.]+\)$/g, '0.5)');
//									}
//									return "rgba(0, 0, 0, 0.5)";
//								},
								formatter: function(value, context) {
									var rt = calcPiePercent(context, 'f');
									return "{0}\r\n{1}건\r\n{2}%".format(rt.n,rt.v,rt.p);
								}
							}
						}
					}, obj, 1)
	});
}

function calcPiePercent2(dataset, i) {
	var rt = {p:0,v:0};
	rt.v = dataset[i];
	
	var total = rt.v;
	if (dataset) {
		total = dataset.reduce(function(previousValue, currentValue, currentIndex, array) {
			return previousValue*1 + currentValue*1;
		});
	}
	rt.p = Math.floor(((rt.v/total) * 100)+0.5);
	
	return rt;
}
function calcPiePercent(context, type) {
	var rt = {n:'',p:0,v:0};
	rt.v = context.chart.data.datasets[0].data[context.dataIndex];
	
	var dataset = context.chart.data.datasets[0];
	var total = rt.v;
	if (dataset) {
		total = dataset.data.reduce(function(previousValue, currentValue, currentIndex, array) {
			return previousValue*1 + currentValue*1;
		});
	}
	
	rt.p = Math.floor(((rt.v/total) * 100)+0.5);
	if (type) {
		rt.n = context.chart.data.labels[context.dataIndex];
	}
	
	return rt;
}

function mixedChart(data, obj) {
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
	
	var ctx = $('#'+obj.id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'bar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options: 	chtOpt({
						title: {display: true, text: data.name},
						plugins: {
							datalabels: {
								align : function(context) {
									var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "end";
				        			}
					        		return "start";
					        	},
					        	anchor : function(context) {
				        			var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "start";
				        			}
					        		return "end";
					        	},
					        	color: function(context) {
									if (context.dataset.type == 'line') {
										if (context.dataset.borderColor) {
											return context.dataset.borderColor.replace(/[\d\.]+\)$/g, '1)');
										} else if (context.dataset.backgroundColor) {
											if (Array.isArray(context.dataset.backgroundColor)) {
												return context.dataset.backgroundColor[context.dataIndex].replace(/[\d\.]+\)$/g, '1)');
											}
											return context.dataset.backgroundColor.replace(/[\d\.]+\)$/g, '1)');
										}
									}
									return "rgba(0, 0, 0, 0.7)";
								}
							}
						}
					}, obj)
	});
}

function mixedChart2(data, obj) {
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
	
	var ctx = $('#'+obj.id);
	ctx.innerHTML = "";
	
	new Chart(ctx, {
		type: 'bar',
		responsive: false,
		data: {
			labels: label,
			datasets: datasets
		},
		options:  	chtOpt({
						title: {display: true, text: data.name},
						plugins: {
							datalabels: {
								align : function(context) {
									var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "end";
				        			}
					        		return "start";
					        	},
					        	anchor : function(context) {
				        			var value = context.chart.data.datasets[0].data[context.dataIndex];
				        			if (value < 0) {
				        				return "start";
				        			}
					        		return "end";
					        	},
					        	color: function(context) {
									if (context.dataset.type == 'line') {
										if (context.dataset.borderColor) {
											return context.dataset.borderColor.replace(/[\d\.]+\)$/g, '1)');
										} else if (context.dataset.backgroundColor) {
											if (Array.isArray(context.dataset.backgroundColor)) {
												return context.dataset.backgroundColor[context.dataIndex].replace(/[\d\.]+\)$/g, '1)');
											}
											return context.dataset.backgroundColor.replace(/[\d\.]+\)$/g, '1)');
										}
									}
									return "rgba(0, 0, 0, 0.7)";
								}
							}
						}
					}, obj)
	});
}

function bubbleChart(data, obj) {
	var datasets = []; var i=0;
	$.each(data.dataSet, function (key, value) {
		var dataset = {};

		dataset.label = key;
		dataset.backgroundColor = randomColorGenerator(i++);
		dataset.data = [{x: value.v1*1, y: value.v2*1, value: value.v1*1+value.v2*1}];
		
		datasets.push(dataset);
	});
	
	var ctx = $('#'+obj.id);
	ctx.innerHTML = "";
	new Chart(ctx, {
		type: 'bubble',
		responsive: false,
		data: {
			datasets: datasets
		},
		options: 	chtOpt({
						title: {display: true, text: data.name},
						legend: { display: false },
						elements: {
							point: {
								radius: function(context) {
									var d = context.chart.data.datasets;
									var maxV=0;
									if (d) {
										for (i=0; i<d.length; i++) {
											maxV = Math.max(maxV, d[i].data[0].value);
										}	
									}
									return bubbleRadius(context, maxV).v;
								}
							}
						},
						plugins: {
							datalabels: {
								align : function(context) {
									return bubbleRadius(context).v < 30 ? 'end' : 'center';
					        	},
					        	anchor : function(context) {
					        		return bubbleRadius(context).v < 30 ? 'end' : 'center';
					        	},
					        	color: function(context) {
					        		return "rgba(0, 0, 0, 0.7)";
								},
								backgroundColor: function(context) {
									return bubbleRadius(context).v < 30 ? context.dataset.backgroundColor.replace(/[\d\.]+\)$/g, '0.7)') : null;
								},
		 						formatter: function(v, c) {
		 							return "{0}\r\n{1}건".format(c.dataset.label, Math.round(v.value));
		 						}
							}
						}
					}, obj)
	});
}


function bubbleRadius(context, maxV) {
	var option = {s:1,b:1,v:100};
	if (maxV) {
		var index = context.dataIndex;
		var data = context.dataset.data[index];
		var size = context.chart.width;
		var base = Math.abs(data.value);
		
		option.s = size;
		option.b = base;
		option.v = base/maxV*100;
	}
	
	return option;
}






















































