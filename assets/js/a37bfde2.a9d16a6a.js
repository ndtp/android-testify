"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[1785],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>m});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var s=r.createContext({}),l=function(e){var t=r.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},p=function(e){var t=l(e.components);return r.createElement(s.Provider,{value:t},e.children)},d="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},f=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,o=e.originalType,s=e.parentName,p=c(e,["components","mdxType","originalType","parentName"]),d=l(n),f=a,m=d["".concat(s,".").concat(f)]||d[f]||u[f]||o;return n?r.createElement(m,i(i({ref:t},p),{},{components:n})):r.createElement(m,i({ref:t},p))}));function m(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=n.length,i=new Array(o);i[0]=f;var c={};for(var s in t)hasOwnProperty.call(t,s)&&(c[s]=t[s]);c.originalType=e,c[d]="string"==typeof e?e:a,i[1]=c;for(var l=2;l<o;l++)i[l]=n[l];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}f.displayName="MDXCreateElement"},4136:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>s,contentTitle:()=>i,default:()=>d,frontMatter:()=>o,metadata:()=>c,toc:()=>l});var r=n(7462),a=(n(7294),n(3905));const o={},i="Increase the matching tolerance",c={unversionedId:"recipes/tolerance",id:"version-2.0.0-rc02/recipes/tolerance",title:"Increase the matching tolerance",description:"In some cases, the captured screenshot may inherently contain randomness. It may then be desirable to allow for an inexact matching. By default, Testify employs an exact, pixel-by-pixel matching algorithm.",source:"@site/versioned_docs/version-2.0.0-rc02/recipes/4-tolerance.md",sourceDirName:"recipes",slug:"/recipes/tolerance",permalink:"/android-testify/docs/2.0.0-rc02/recipes/tolerance",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-rc02/recipes/4-tolerance.md",tags:[],version:"2.0.0-rc02",sidebarPosition:4,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Changing the font scale in a test",permalink:"/android-testify/docs/2.0.0-rc02/recipes/font-scale"},next:{title:"Using @TestifyLayout with library projects",permalink:"/android-testify/docs/2.0.0-rc02/recipes/testify-layout-library"}},s={},l=[],p={toc:l};function d(e){let{components:t,...n}=e;return(0,a.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"increase-the-matching-tolerance"},"Increase the matching tolerance"),(0,a.kt)("p",null,"In some cases, the captured screenshot may inherently contain randomness. It may then be desirable to allow for an inexact matching. By default, Testify employs an exact, pixel-by-pixel matching algorithm.\nAlternatively, you may optionally reduce this exactness."),(0,a.kt)("p",null,"By providing a value less than 1 to ",(0,a.kt)("inlineCode",{parentName:"p"},"setExactness"),", a test will be more tolerant to color differences. A value of less than 1 will enable the ",(0,a.kt)("a",{parentName:"p",href:"https://en.wikipedia.org/wiki/Color_difference#CIELAB_%CE%94E"},(0,a.kt)("em",{parentName:"a"},"Delta E"))," comparison method. The Delta E algorithm will mathematically quantify the similarity between two different colors. It ignores differences between two pixels that the human eye would consider identical while still identifying differences in position, size or layout."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @TestifyLayout(R.layout.view_client_details)\n    @ScreenshotInstrumentation\n    @Test\n    fun setExactness() {\n        rule\n            .setExactness(0.9f)\n            .setViewModifications {\n                val r = Integer.toHexString(Random.nextInt(0, 25) + 230).padStart(2, '0')\n                it.findViewById<View>(R.id.info_card).setBackgroundColor(Color.parseColor(\"#${r}0000\"))\n            }\n            .assertSame()\n    }\n")))}d.isMDXComponent=!0}}]);