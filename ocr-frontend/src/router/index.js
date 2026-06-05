import { createRouter, createWebHistory } from 'vue-router';
import UploadView from "../components/UploadForm.vue";
import ScannedDocuments from "../components/ScannedDocuments.vue";
import DocumentContent from "../components/DocumentContent.vue";
import UploadForm from "../components/UploadForm.vue";
import FaqComponent from "../components/FaqComponent.vue";

const routes = [
    { path: "/", component: UploadForm },
    { path: "/documents", component: ScannedDocuments },
    { path: '/document/:id', component: DocumentContent, props: true },
    {path: '/faq',component: FaqComponent}
];


const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;