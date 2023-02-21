"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[3704],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>f});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var c=r.createContext({}),l=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},p=function(e){var t=l(e.components);return r.createElement(c.Provider,{value:t},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,o=e.originalType,c=e.parentName,p=s(e,["components","mdxType","originalType","parentName"]),u=l(n),m=a,f=u["".concat(c,".").concat(m)]||u[m]||d[m]||o;return n?r.createElement(f,i(i({ref:t},p),{},{components:n})):r.createElement(f,i({ref:t},p))}));function f(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=n.length,i=new Array(o);i[0]=m;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s[u]="string"==typeof e?e:a,i[1]=s;for(var l=2;l<o;l++)i[l]=n[l];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},1718:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>u,frontMatter:()=>o,metadata:()=>s,toc:()=>l});var r=n(7462),a=(n(7294),n(3905));const o={},i="Increase the matching tolerance",s={unversionedId:"recipes/tolerance",id:"version-1.2.0-alpha01/recipes/tolerance",title:"Increase the matching tolerance",description:"In some cases, the captured screenshot may inherently contain randomness. It may then be desirable to allow for an inexact matching. By default, Testify employs an exact, pixel-by-pixel matching algorithm.",source:"@site/versioned_docs/version-1.2.0-alpha01/recipes/4-tolerance.md",sourceDirName:"recipes",slug:"/recipes/tolerance",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/tolerance",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-1.2.0-alpha01/recipes/4-tolerance.md",tags:[],version:"1.2.0-alpha01",sidebarPosition:4,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Changing the font scale in a test",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/font-scale"},next:{title:"Using @TestifyLayout with library projects",permalink:"/android-testify/docs/1.2.0-alpha01/recipes/testify-layout-library"}},c={},l=[],p={toc:l};function u(e){let{components:t,...n}=e;return(0,a.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"increase-the-matching-tolerance"},"Increase the matching tolerance"),(0,a.kt)("p",null,"In some cases, the captured screenshot may inherently contain randomness. It may then be desirable to allow for an inexact matching. By default, Testify employs an exact, pixel-by-pixel matching algorithm.\nAlternatively, you may optionally reduce this exactness."),(0,a.kt)("p",null,"By providing a value less than 1 to ",(0,a.kt)("inlineCode",{parentName:"p"},"setExactness"),", a test will be more tolerant to color differences. The fuzzy matching algorithm maps the captured image into the HSV color space\nand compares the Hue, Saturation and Lightness components of each pixel. If they are within the provided tolerance, the images are considered to be the same."),(0,a.kt)("p",null,"\u26a0\ufe0f Note that the fuzzy matching is approximately 10x slower than the default matching.\n",(0,a.kt)("strong",{parentName:"p"},"Use sparingly.")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @TestifyLayout(R.layout.view_client_details)\n    @ScreenshotInstrumentation\n    @Test\n    fun setExactness() {\n        rule\n            .setExactness(0.9f)\n            .setViewModifications {\n                val r = Integer.toHexString(Random.nextInt(0, 25) + 230).padStart(2, '0')\n                it.findViewById<View>(R.id.info_card).setBackgroundColor(Color.parseColor(\"#${r}0000\"))\n            }\n            .assertSame()\n    }\n")))}u.isMDXComponent=!0}}]);