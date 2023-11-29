"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[3089],{9058:(e,t,a)=>{a.d(t,{Z:()=>h});var l=a(7294),r=a(6010),n=a(3659),i=a(7524),s=a(9960),o=a(5999);const m={sidebar:"sidebar_re4s",sidebarItemTitle:"sidebarItemTitle_pO2u",sidebarItemList:"sidebarItemList_Yudw",sidebarItem:"sidebarItem__DBe",sidebarItemLink:"sidebarItemLink_mo7H",sidebarItemLinkActive:"sidebarItemLinkActive_I1ZP"};function c(e){let{sidebar:t}=e;return l.createElement("aside",{className:"col col--3"},l.createElement("nav",{className:(0,r.Z)(m.sidebar,"thin-scrollbar"),"aria-label":(0,o.I)({id:"theme.blog.sidebar.navAriaLabel",message:"Blog recent posts navigation",description:"The ARIA label for recent posts in the blog sidebar"})},l.createElement("div",{className:(0,r.Z)(m.sidebarItemTitle,"margin-bottom--md")},t.title),l.createElement("ul",{className:(0,r.Z)(m.sidebarItemList,"clean-list")},t.items.map((e=>l.createElement("li",{key:e.permalink,className:m.sidebarItem},l.createElement(s.Z,{isNavLink:!0,to:e.permalink,className:m.sidebarItemLink,activeClassName:m.sidebarItemLinkActive},e.title)))))))}var u=a(3102);function g(e){let{sidebar:t}=e;return l.createElement("ul",{className:"menu__list"},t.items.map((e=>l.createElement("li",{key:e.permalink,className:"menu__list-item"},l.createElement(s.Z,{isNavLink:!0,to:e.permalink,className:"menu__link",activeClassName:"menu__link--active"},e.title)))))}function d(e){return l.createElement(u.Zo,{component:g,props:e})}function p(e){let{sidebar:t}=e;const a=(0,i.i)();return t?.items.length?"mobile"===a?l.createElement(d,{sidebar:t}):l.createElement(c,{sidebar:t}):null}function h(e){const{sidebar:t,toc:a,children:i,...s}=e,o=t&&t.items.length>0;return l.createElement(n.Z,s,l.createElement("div",{className:"container margin-vert--lg"},l.createElement("div",{className:"row"},l.createElement(p,{sidebar:t}),l.createElement("main",{className:(0,r.Z)("col",{"col--7":o,"col--9 col--offset-1":!o}),itemScope:!0,itemType:"http://schema.org/Blog"},i),a&&l.createElement("div",{className:"col col--2"},a))))}},46:(e,t,a)=>{a.r(t),a.d(t,{default:()=>p});var l=a(7294),r=a(6010),n=a(2263),i=a(833),s=a(5281),o=a(9058),m=a(1860),c=a(9703),u=a(197);function g(e){const{metadata:t}=e,{siteConfig:{title:a}}=(0,n.Z)(),{blogDescription:r,blogTitle:s,permalink:o}=t,m="/"===o?a:s;return l.createElement(l.Fragment,null,l.createElement(i.d,{title:m,description:r}),l.createElement(u.Z,{tag:"blog_posts_list"}))}function d(e){const{metadata:t,items:a,sidebar:r}=e;return l.createElement(o.Z,{sidebar:r},a.map((e=>{let{content:t}=e;return l.createElement(m.Z,{key:t.metadata.permalink,frontMatter:t.frontMatter,assets:t.assets,metadata:t.metadata,truncated:t.metadata.truncated},l.createElement(t,null))})),l.createElement(c.Z,{metadata:t}))}function p(e){return l.createElement(i.FG,{className:(0,r.Z)(s.k.wrapper.blogPages,s.k.page.blogListPage)},l.createElement(g,e),l.createElement(d,e))}},9703:(e,t,a)=>{a.d(t,{Z:()=>i});var l=a(7294),r=a(5999),n=a(2244);function i(e){const{metadata:t}=e,{previousPage:a,nextPage:i}=t;return l.createElement("nav",{className:"pagination-nav","aria-label":(0,r.I)({id:"theme.blog.paginator.navAriaLabel",message:"Blog list page navigation",description:"The ARIA label for the blog pagination"})},a&&l.createElement(n.Z,{permalink:a,title:l.createElement(r.Z,{id:"theme.blog.paginator.newerEntries",description:"The label used to navigate to the newer blog posts page (previous page)"},"Newer Entries")}),i&&l.createElement(n.Z,{permalink:i,title:l.createElement(r.Z,{id:"theme.blog.paginator.olderEntries",description:"The label used to navigate to the older blog posts page (next page)"},"Older Entries"),isNext:!0}))}},1860:(e,t,a)=>{a.d(t,{Z:()=>v});var l=a(7294),r=a(6010),n=a(5999),i=a(9960),s=a(4996),o=a(8824),m=a(8780),c=a(3548),u=a(6114),g=a(1526);function d(e){return e.href?l.createElement(i.Z,e):l.createElement(l.Fragment,null,e.children)}function p(e){let{author:t}=e;const{name:a,title:r,url:n,imageURL:i,email:s}=t,o=n||s&&`mailto:${s}`||void 0;return l.createElement("div",{className:"avatar margin-bottom--sm"},i&&l.createElement(d,{href:o,className:"avatar__photo-link"},l.createElement("img",{className:"avatar__photo",src:i,alt:a})),a&&l.createElement("div",{className:"avatar__intro",itemProp:"author",itemScope:!0,itemType:"https://schema.org/Person"},l.createElement("div",{className:"avatar__name"},l.createElement(d,{href:o,itemProp:"url"},l.createElement("span",{itemProp:"name"},a))),r&&l.createElement("small",{className:"avatar__subtitle",itemProp:"description"},r)))}const h={authorCol:"authorCol_sTYa",imageOnlyAuthorRow:"imageOnlyAuthorRow_vA2J",imageOnlyAuthorCol:"imageOnlyAuthorCol_kG3X"};function b(e){let{authors:t,assets:a}=e;if(0===t.length)return null;const n=t.every((e=>{let{name:t}=e;return!t}));return l.createElement("div",{className:(0,r.Z)("margin-top--md margin-bottom--sm",n?h.imageOnlyAuthorRow:"row")},t.map(((e,t)=>l.createElement("div",{className:(0,r.Z)(!n&&"col col--6",n?h.imageOnlyAuthorCol:h.authorCol),key:t},l.createElement(p,{author:{...e,imageURL:a.authorsImageUrls[t]??e.imageURL}})))))}const E={blogPostTitle:"blogPostTitle_Ikge",blogPostData:"blogPostData_SAv4",blogPostDetailsFull:"blogPostDetailsFull_u0Nl"};function v(e){const t=function(){const{selectMessage:e}=(0,o.c)();return t=>{const a=Math.ceil(t);return e(a,(0,n.I)({id:"theme.blog.post.readingTime.plurals",description:'Pluralized label for "{readingTime} min read". Use as much plural forms (separated by "|") as your language support (see https://www.unicode.org/cldr/cldr-aux/charts/34/supplemental/language_plural_rules.html)',message:"One min read|{readingTime} min read"},{readingTime:a}))}}(),{withBaseUrl:a}=(0,s.C)(),{children:d,frontMatter:p,assets:h,metadata:v,truncated:f,isBlogPostPage:_=!1}=e,{date:Z,formattedDate:k,permalink:N,tags:P,readingTime:I,title:T,editUrl:w,authors:L}=v,y=h.image??p.image,A=!_&&f,C=P.length>0,F=_?"h1":"h2";return l.createElement("article",{className:_?void 0:"margin-bottom--xl",itemProp:"blogPost",itemScope:!0,itemType:"http://schema.org/BlogPosting"},l.createElement("header",null,l.createElement(F,{className:E.blogPostTitle,itemProp:"headline"},_?T:l.createElement(i.Z,{itemProp:"url",to:N},T)),l.createElement("div",{className:(0,r.Z)(E.blogPostData,"margin-vert--md")},l.createElement("time",{dateTime:Z,itemProp:"datePublished"},k),void 0!==I&&l.createElement(l.Fragment,null," \xb7 ",t(I))),l.createElement(b,{authors:L,assets:h})),y&&l.createElement("meta",{itemProp:"image",content:a(y,{absolute:!0})}),l.createElement("div",{id:_?m.blogPostContainerID:void 0,className:"markdown",itemProp:"articleBody"},l.createElement(c.Z,null,d)),(C||f)&&l.createElement("footer",{className:(0,r.Z)("row docusaurus-mt-lg",_&&E.blogPostDetailsFull)},C&&l.createElement("div",{className:(0,r.Z)("col",{"col--9":A})},l.createElement(g.Z,{tags:P})),_&&w&&l.createElement("div",{className:"col margin-top--sm"},l.createElement(u.Z,{editUrl:w})),A&&l.createElement("div",{className:(0,r.Z)("col text--right",{"col--3":C})},l.createElement(i.Z,{to:v.permalink,"aria-label":(0,n.I)({message:"Read more about {title}",id:"theme.blog.post.readMoreLabel",description:"The ARIA label for the link to full blog posts from excerpts"},{title:T})},l.createElement("b",null,l.createElement(n.Z,{id:"theme.blog.post.readMore",description:"The label used in blog post item excerpts to link to full blog posts"},"Read More"))))))}},6114:(e,t,a)=>{a.d(t,{Z:()=>c});var l=a(7294),r=a(5999),n=a(5281),i=a(7462),s=a(6010);const o={iconEdit:"iconEdit_eYIM"};function m(e){let{className:t,...a}=e;return l.createElement("svg",(0,i.Z)({fill:"currentColor",height:"20",width:"20",viewBox:"0 0 40 40",className:(0,s.Z)(o.iconEdit,t),"aria-hidden":"true"},a),l.createElement("g",null,l.createElement("path",{d:"m34.5 11.7l-3 3.1-6.3-6.3 3.1-3q0.5-0.5 1.2-0.5t1.1 0.5l3.9 3.9q0.5 0.4 0.5 1.1t-0.5 1.2z m-29.5 17.1l18.4-18.5 6.3 6.3-18.4 18.4h-6.3v-6.2z"})))}function c(e){let{editUrl:t}=e;return l.createElement("a",{href:t,target:"_blank",rel:"noreferrer noopener",className:n.k.common.editThisPage},l.createElement(m,null),l.createElement(r.Z,{id:"theme.common.editThisPage",description:"The link label to edit the current page"},"Edit this page"))}},2244:(e,t,a)=>{a.d(t,{Z:()=>i});var l=a(7294),r=a(6010),n=a(9960);function i(e){const{permalink:t,title:a,subLabel:i,isNext:s}=e;return l.createElement(n.Z,{className:(0,r.Z)("pagination-nav__link",s?"pagination-nav__link--next":"pagination-nav__link--prev"),to:t},i&&l.createElement("div",{className:"pagination-nav__sublabel"},i),l.createElement("div",{className:"pagination-nav__label"},a))}},3008:(e,t,a)=>{a.d(t,{Z:()=>s});var l=a(7294),r=a(6010),n=a(9960);const i={tag:"tag_zVej",tagRegular:"tagRegular_sFm0",tagWithCount:"tagWithCount_h2kH"};function s(e){let{permalink:t,label:a,count:s}=e;return l.createElement(n.Z,{href:t,className:(0,r.Z)(i.tag,s?i.tagWithCount:i.tagRegular)},a,s&&l.createElement("span",null,s))}},1526:(e,t,a)=>{a.d(t,{Z:()=>o});var l=a(7294),r=a(6010),n=a(5999),i=a(3008);const s={tags:"tags_jXut",tag:"tag_QGVx"};function o(e){let{tags:t}=e;return l.createElement(l.Fragment,null,l.createElement("b",null,l.createElement(n.Z,{id:"theme.tags.tagsListLabel",description:"The label alongside a tag list"},"Tags:")),l.createElement("ul",{className:(0,r.Z)(s.tags,"padding--none","margin-left--sm")},t.map((e=>{let{label:t,permalink:a}=e;return l.createElement("li",{key:a,className:s.tag},l.createElement(i.Z,{label:t,permalink:a}))}))))}},8824:(e,t,a)=>{a.d(t,{c:()=>m});var l=a(7294),r=a(2263);const n=["zero","one","two","few","many","other"];function i(e){return n.filter((t=>e.includes(t)))}const s={locale:"en",pluralForms:i(["one","other"]),select:e=>1===e?"one":"other"};function o(){const{i18n:{currentLocale:e}}=(0,r.Z)();return(0,l.useMemo)((()=>{try{return function(e){const t=new Intl.PluralRules(e);return{locale:e,pluralForms:i(t.resolvedOptions().pluralCategories),select:e=>t.select(e)}}(e)}catch(t){return console.error(`Failed to use Intl.PluralRules for locale "${e}".\nDocusaurus will fallback to the default (English) implementation.\nError: ${t.message}\n`),s}}),[e])}function m(){const e=o();return{selectMessage:(t,a)=>function(e,t,a){const l=e.split("|");if(1===l.length)return l[0];l.length>a.pluralForms.length&&console.error(`For locale=${a.locale}, a maximum of ${a.pluralForms.length} plural forms are expected (${a.pluralForms.join(",")}), but the message contains ${l.length}: ${e}`);const r=a.select(t),n=a.pluralForms.indexOf(r);return l[Math.min(n,l.length-1)]}(a,t,e)}}}}]);