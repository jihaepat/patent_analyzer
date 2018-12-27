








// date picker
$.fn.datepicker.dates['en'] = {
    days: ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"],
    daysShort: ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"],
    daysMin: ["일", "월", "화", "수", "목", "금", "토"],
    months: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
    monthsShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
    today: "오늘",
    clear: "취소",
    format: "yyyy-mm-dd",
    titleFormat: "yyyy mm", /* Leverages same syntax as 'format' */
    weekStart: 0
};
jQuery.validator.addMethod("validRegDate", function(value, element) { 
	return value.match(/^(19|20)\d\d-(0\d|1[012])-(0\d|1\d|2\d|3[01])$/);
}, "날짜 형식을 년-월-일 형식으로 입력하십시오.");

function allClearForm(id) {
//	$('#'+id).find('form')[0].reset();
	$("#"+id+" input:hidden, #"+id+" :text").val("");
	if($("#"+id)[0]) {
		$("#"+id)[0].reset()
	}
}




function sweetalert(title, type, func) {
	var option = {
		allowOutsideClick: false,
        allowEscapeKey: false,
        allowEnterKey: false
    };
	
	option["text"] = title;
	if (type)	option["type"] = type;
	if (!func)	func = function() {}
	
	swal(option, func);
}

function sweetconfirm(title, type, confirmFunc, cancelFunc) {
	var option = {
		allowOutsideClick: false,
        allowEscapeKey: false,
        allowEnterKey: false,
        showCancelButton: true,
        closeOnConfirm: false,
        cancelButtonText: "아니요",
		confirmButtonText: "예"
    };
	
	option["text"] = title;
	if (type)	option["type"] = type;
	if (!confirmFunc)	confirmFunc = function() {}
	if (!cancelFunc)	cancelFunc = function() {}
	
	swal(option, function(isConfirm) {
		if (isConfirm) {
			myTimeout(confirmFunc);
		} else {
			myTimeout(cancelFunc);
		}
		
		swal.close();
	});
}

function swalMessage(status, check) {
	if (status > check) {
		return "설정정보에 따라 (재)실행됩니다.\n저장하시겠습니까?";
	} else {
		return "저장하시겠습니까?";
	}
}

function sweetsubmit(options, func) {
	options = jQuery.extend({
			allowOutsideClick: false,
	        allowEscapeKey: false,
	        allowEnterKey: false,
			showCancelButton: true,
			closeOnConfirm: false,
			showLoaderOnConfirm: true,
			cancelButtonText: "취소",
			confirmButtonText: "저장",
			text: ""
    	}, options);

	swal(options, func);
}


var tmp_timeout;
function myTimeout(func, tm) {
	if (!tm) {
		tm = 100;
	}
	clearTimeout(tmp_timeout);
	tmp_timeout = setTimeout(func, tm);
}



//Collapse ibox function
function inspiniaCollapseLink() {
    var ibox = $(this).closest('div.ibox');
    var button = $(this).find('i');
    var content = ibox.find('div.ibox-content');
    
    content.slideToggle(200);
    button.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
    ibox.toggleClass('').toggleClass('border-bottom');
    setTimeout(function () {
        ibox.resize();
        ibox.find('[id^=map-]').resize();
    }, 50);
}



function serialize(form) {
}

function eventSpinner() {
	$(".modal").off("show.bs.modal").on("show.bs.modal", function () {
		if ($(".modal-backdrop").length == 1) {
			$(".modal-backdrop").show();
		}
	});
	$(".modal").off("hidden.bs.modal").on("hidden.bs.modal", function () {
		if ($(".modal:visible").length == 0) {
			$(".modal-backdrop").hide();
		}
	});
}



$(function() {
	
	$.validator.addMethod('phone', function (value) {
		if (value) {
			return /^\d{2,3}-\d{3,4}-\d{4}$/.test(value);	
		}
	    return true;
	}, '연락처를 정확하게 입력하십시요.');
	
});