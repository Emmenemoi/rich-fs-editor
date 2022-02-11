//import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
//import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor.js';
import CustomEditor from './ckeditor.js';
import EditorManager from 'ckeditor5-webcomponent/dist/collection/editor-manager.js';
import { defineCustomElements } from 'ckeditor5-webcomponent/dist/esm/es2017/x-ckeditor.define.js';
//import ClassicEditor from "@ckeditor/ckeditor5-editor-classic/src/classiceditor";
//import Markdown from '@ckeditor/ckeditor5-markdown-gfm/src/markdown';


defineCustomElements(window);

// We register the ClassicEditor under the name 'classic'
//EditorManager.register('classic', ClassicEditor);

EditorManager.register('custom', CustomEditor);