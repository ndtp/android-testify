"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[2659],{3905:(e,r,n)=>{n.d(r,{Zo:()=>p,kt:()=>m});var t=n(7294);function o(e,r,n){return r in e?Object.defineProperty(e,r,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[r]=n,e}function i(e,r){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);r&&(t=t.filter((function(r){return Object.getOwnPropertyDescriptor(e,r).enumerable}))),n.push.apply(n,t)}return n}function a(e){for(var r=1;r<arguments.length;r++){var n=null!=arguments[r]?arguments[r]:{};r%2?i(Object(n),!0).forEach((function(r){o(e,r,n[r])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(r){Object.defineProperty(e,r,Object.getOwnPropertyDescriptor(n,r))}))}return e}function s(e,r){if(null==e)return{};var n,t,o=function(e,r){if(null==e)return{};var n,t,o={},i=Object.keys(e);for(t=0;t<i.length;t++)n=i[t],r.indexOf(n)>=0||(o[n]=e[n]);return o}(e,r);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(t=0;t<i.length;t++)n=i[t],r.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var c=t.createContext({}),l=function(e){var r=t.useContext(c),n=r;return e&&(n="function"==typeof e?e(r):a(a({},r),e)),n},p=function(e){var r=l(e.components);return t.createElement(c.Provider,{value:r},e.children)},d="mdxType",u={inlineCode:"code",wrapper:function(e){var r=e.children;return t.createElement(t.Fragment,{},r)}},f=t.forwardRef((function(e,r){var n=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,p=s(e,["components","mdxType","originalType","parentName"]),d=l(n),f=o,m=d["".concat(c,".").concat(f)]||d[f]||u[f]||i;return n?t.createElement(m,a(a({ref:r},p),{},{components:n})):t.createElement(m,a({ref:r},p))}));function m(e,r){var n=arguments,o=r&&r.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=f;var s={};for(var c in r)hasOwnProperty.call(r,c)&&(s[c]=r[c]);s.originalType=e,s[d]="string"==typeof e?e:o,a[1]=s;for(var l=2;l<i;l++)a[l]=n[l];return t.createElement.apply(null,a)}return t.createElement.apply(null,n)}f.displayName="MDXCreateElement"},3629:(e,r,n)=>{n.r(r),n.d(r,{assets:()=>c,contentTitle:()=>a,default:()=>d,frontMatter:()=>i,metadata:()=>s,toc:()=>l});var t=n(7462),o=(n(7294),n(3905));const i={},a="Excluding a region from the comparison",s={unversionedId:"recipes/exclude-regions",id:"version-2.0.0-alpha02/recipes/exclude-regions",title:"Excluding a region from the comparison",description:"For some Views, it may be impossible to guarantee a stable, consistent rendering. For instance, if the content is dynamic or randomized.",source:"@site/versioned_docs/version-2.0.0-alpha02/recipes/14-exclude-regions.md",sourceDirName:"recipes",slug:"/recipes/exclude-regions",permalink:"/android-testify/docs/2.0.0-alpha02/recipes/exclude-regions",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-alpha02/recipes/14-exclude-regions.md",tags:[],version:"2.0.0-alpha02",sidebarPosition:14,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Force software rendering",permalink:"/android-testify/docs/2.0.0-alpha02/recipes/software-rendering"},next:{title:"Placing the keyboard focus on a specific view",permalink:"/android-testify/docs/2.0.0-alpha02/recipes/keyboard-focus"}},c={},l=[],p={toc:l};function d(e){let{components:r,...n}=e;return(0,o.kt)("wrapper",(0,t.Z)({},p,n,{components:r,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"excluding-a-region-from-the-comparison"},"Excluding a region from the comparison"),(0,o.kt)("p",null,"For some Views, it may be impossible to guarantee a stable, consistent rendering. For instance, if the content is dynamic or randomized.\nFor this reason, Testify provides the option to specify a series of rectangles to exclude from the comparison.\nAll pixels in these rectangles are ignored and only pixels not contained will be compared."),(0,o.kt)("p",null,"Note that this comparison mechanism is slower than the default."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule\n            .defineExclusionRects { rootView, exclusionRects ->\n                val card = rootView.findViewById<View>(R.id.info_card)\n                exclusionRects.add(card.boundingBox)\n            }\n            .assertSame()\n    }\n")))}d.isMDXComponent=!0}}]);