"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[6692],{3905:function(e,t,r){r.d(t,{Zo:function(){return p},kt:function(){return f}});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function c(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var s=n.createContext({}),u=function(e){var t=n.useContext(s),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},p=function(e){var t=u(e.components);return n.createElement(s.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},l=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,a=e.originalType,s=e.parentName,p=c(e,["components","mdxType","originalType","parentName"]),l=u(r),f=o,y=l["".concat(s,".").concat(f)]||l[f]||d[f]||a;return r?n.createElement(y,i(i({ref:t},p),{},{components:r})):n.createElement(y,i({ref:t},p))}));function f(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=r.length,i=new Array(a);i[0]=l;var c={};for(var s in t)hasOwnProperty.call(t,s)&&(c[s]=t[s]);c.originalType=e,c.mdxType="string"==typeof e?e:o,i[1]=c;for(var u=2;u<a;u++)i[u]=r[u];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}l.displayName="MDXCreateElement"},2287:function(e,t,r){r.r(t),r.d(t,{assets:function(){return p},contentTitle:function(){return s},default:function(){return f},frontMatter:function(){return c},metadata:function(){return u},toc:function(){return d}});var n=r(7462),o=r(3366),a=(r(7294),r(3905)),i=["components"],c={},s="Placing the keyboard focus on a specific view",u={unversionedId:"recipes/keyboard-focus",id:"recipes/keyboard-focus",title:"Placing the keyboard focus on a specific view",description:"It's possible that users can navigate your app using a keyboard, because the Android system enables most of the necessary behaviors by default.",source:"@site/docs/recipes/15-keyboard-focus.md",sourceDirName:"recipes",slug:"/recipes/keyboard-focus",permalink:"/android-testify/docs/recipes/keyboard-focus",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/recipes/15-keyboard-focus.md",tags:[],version:"current",sidebarPosition:15,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Excluding a region from the comparison",permalink:"/android-testify/docs/recipes/exclude-regions"},next:{title:"Customizing the captured bitmap",permalink:"/android-testify/docs/recipes/custom-bitmap"}},p={},d=[],l={toc:d};function f(e){var t=e.components,r=(0,o.Z)(e,i);return(0,a.kt)("wrapper",(0,n.Z)({},l,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"placing-the-keyboard-focus-on-a-specific-view"},"Placing the keyboard focus on a specific view"),(0,a.kt)("p",null,"It's possible that users can navigate your app using a keyboard, because the Android system enables most of the necessary behaviors by default.\nIn order to place the keyboard focus on a specific View, use the ",(0,a.kt)("inlineCode",{parentName:"p"},"setFocusTarget")," method."),(0,a.kt)("p",null,"See ",(0,a.kt)("a",{parentName:"p",href:"https://developer.android.com/training/keyboard-input/navigation"},"https://developer.android.com/training/keyboard-input/navigation")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule\n            .setFocusTarget(enabled = true, focusTargetId = R.id.fab)\n            .assertSame()\n    }\n")))}f.isMDXComponent=!0}}]);