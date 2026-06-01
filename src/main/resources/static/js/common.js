$(function () {
    let navToggle = $(".nav__toggle");
let navWrapper = $(".nav__wrapper");

navToggle.on("click", function () {
  if (navWrapper.hasClass("active")) {
    $(this).attr("aria-expanded", "false");
    $(this).attr("aria-label", "menu");
    navWrapper.removeClass("active");
  } else {
    navWrapper.addClass("active");
    $(this).attr("aria-label", "close menu");
    $(this).attr("aria-expanded", "true");
    searchForm.removeClass("active");
  }
});

let searchToggle = $(".search__toggle");
let searchForm = $(".search__form");

searchToggle.on("click", function() {
  searchForm.toggleClass("active");
  navToggle.attr("aria-expanded", "false");
  navToggle.attr("aria-label", "menu");
  navWrapper.removeClass("active");
});

/*--------------さくらが舞い散る------------------*/

// 'js-cherry'クラスがついた要素を全て取得
const cherryEls = $(".js-cherry");

// 取得した要素をArrayに変換
const cherryElsArr = $.makeArray(cherryEls);

// 取得した要素ひとつひとつに処理を行う
$.each(cherryElsArr, function(index, cherryEl) {
let interval;

// マウスホバー時に桜を降らせる
cherryEl.addEventListener("mouseenter", function() {
interval = setInterval($.proxy(createPetal, null, cherryEl), 500);
});

// マウスを離すと停止
cherryEl.addEventListener("mouseleave", function() {
clearInterval(interval);
});
});

// 花びらを生成する関数
const createPetal = function(el) {
const petalEl = $("<span></span>").addClass("petal");
const minSize = 10;
const maxSize = 15;
const size = Math.random() * (maxSize + 1 - minSize) + minSize;
petalEl.css({ width: size + "px", height: size + "px", left: Math.random() * el.clientWidth + "px" });
$(el).append(petalEl);

// 一定時間が経てば花びらを消す
setTimeout(function() {
petalEl.remove();
}, 6000);
};
})