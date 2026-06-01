$(function() {

	$('#search').on('click', function() {
		var blogTitle = $('#search').val();

		$.ajax({
			url: '/search',
			dataType: "text",
			type: "GET",
			data: {
				blogTitle: blogTitle
			},
			// Ajaxが正常終了した場合
		}).done(function(data, textStatus, jqXHR) {
			// 該当するデータが無かった場合
			if (!data) {
				alert("該当するデータはありませんでした");
				return;
			}

			// 該当するデータがあった場合は、取得したUserDataオブジェクトのリストを
			// 画面のtableタグに表示
			// その際、名前・性別・メモはデコードする
			const blogList = JSON.parse(data);
			var ins = "";
			$("#products").empty();
			for (i = 0; i < blogList .length; i++) {
				ins = '<a href="/user/blog/edit/' + blogList[i].blogId +'">';
				//ins += '<img src="./itemImage/eraser.png" alt="">';
				ins += '<img src="./itemImage/' + itemList[i].image + '">';
				ins += '<p>';
				ins += itemList[i].itemName;
				ins += '</p>';
				ins += '</a>';
				ins += '</li>';
			}
			$("#products").html(ins);
			// Ajaxが異常終了した場合
		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert("エラーが発生し、データが取得できませんでした");
		});
	});
	$('#search').on('click', function() {
		var categoryId = $('#categoryId').val();
		var itemName = $('#itemName').val();

		$.ajax({
			url: '/searchadminitem',
			dataType: "text",
			type: "GET",
			data: {
				categoryId: categoryId,
				itemName: itemName
			},
			// Ajaxが正常終了した場合
		}).done(function(data, textStatus, jqXHR) {
			// 該当するデータが無かった場合
			if (!data) {
				alert("該当するデータはありませんでした");
				return;
			}

			// 該当するデータがあった場合は、取得したUserDataオブジェクトのリストを
			// 画面のtableタグに表示
			// その際、名前・性別・メモはデコードする
			const itemList = JSON.parse(data);
			var ins = "";
			$("#products").empty();
			for (i = 0; i < itemList.length; i++) {
				ins += '<li>';
				ins += '<a href="/admin/detail/' + itemList[i].itemId + '">';
				ins += '<img src="./itemImage/' + itemList[i].image + '">';
				ins += '<p>';
				ins += itemList[i].itemName;
				ins += '</p>';
				ins += '</a>';
				ins += '</li>';
			}
			$("#products").html(ins);
			// Ajaxが異常終了した場合
		}).fail(function(jqXHR, textStatus, errorThrown) {
			alert("エラーが発生し、データが取得できませんでした");
		});
	});

	

});
