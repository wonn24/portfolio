function msg_empty_fields() {
	swal.fire({
		title : msg3,
		icon : 'error',
		backdrop : false,
		showCancelButton : false,
		cancelButtonText : 'No',
		confirmButtonText : 'OK',
		reverseButtons : true
	});
}

function msg_save_success() {
	swal.fire({
		title : msg3,
		icon : 'error',
		backdrop : false,
		showCancelButton : false,
		cancelButtonText : 'No',
		confirmButtonText : 'OK',
		reverseButtons : true
	}).then(function(result) {
		if (result.value) {
			swal.fire('Saved!', 'text.', 'success');
		}
	});
}

function msg_instant_success_tmp_save() {
	Swal.mixin({
		toast : true,
		position : 'top-end',
		showConfirmButton : false,
		timer : 3000
	}).fire({
		icon : 'success',
		title : 'Temporary saved.'
	});
}

function msg_error() {
	swal.fire({
		title : 'Unexpected error',
		icon : 'error',
		backdrop : false,
		confirmButtonText : 'OK'
	});
}

function msg_save_success() {
	swal.fire({
		title : msg_save_success,
		icon : 'success',
		backdrop : false,
		confirmButtonText : 'OK'
	});
}