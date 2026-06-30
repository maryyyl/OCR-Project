<template>
  <header>
    <div class="header">
      <div class="top-nav">
        <div class="nav-left">
          <div class="nav-buttons">

            <RouterLink class="nav-link-2" to="/">
              Додај нова книга
            </RouterLink>
          </div>

        </div>
        <div class="nav-center">
          <div class="search-container">

            <input
                v-model="searchAuthor"
                @input="liveSearch"
                type="text"
                placeholder="Пребарај автор...🔎"
            />
            <input
                v-model="searchTitle"
                @input="liveSearch"
                type="text"
                placeholder="Пребарај наслов...🔎"
            />
          </div>
          <select v-model="filterModel" @change="liveSearch" class="model-filter">
            <option value="">Сите модели</option>
            <option value="tesseract">⚡ Tesseract</option>
            <option value="googlevision">🌐 Google Vision</option>
          </select>
        </div>

<div class="nav-right">

  <a target="_blank" href="https://www.finki.ukim.mk/mk/content/%D1%84%D0%B8%D0%BD%D0%BA%D0%B8-%D1%81%D0%BE-%D0%BF%D0%BE%D0%B4%D0%B4%D1%80%D1%88%D0%BA%D0%B0-%D0%BD%D0%B0-%D0%BC%D0%B4%D1%82-%D0%B8-%D0%B2%D0%BB%D0%B0%D0%B4%D0%B0%D1%82%D0%B0-%D0%B4%D0%BE%D0%B1%D0%B8-%D0%B5%D0%B2%D1%80%D0%BE%D0%BF%D1%81%D0%BA%D0%BE-%D1%84%D0%B8%D0%BD%D0%B0%D0%BD%D1%81%D0%B8%D1%80%D0%B0%D1%9A%D0%B5-%D0%B7%D0%B0-%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%BE%D1%82-%D0%B2%D0%B5%D0%B7%D0%B8%D0%BB%D0%BA%D0%B0-%D0%B7%D0%B0">
    <img src="/aifa_-_vezilka_-_tranparent%20(1).png" class="logo" />
  </a>
</div>

      </div>
    </div>
  </header>
<div><h1>Збирка на документи</h1></div>

  <div class="message-empty" v-if="documents.length === 0">
    Нема скенирани документи.
  </div>
  <div class="documents-container">

    <div v-for="doc in documents" :key="doc.id" class="doc-card">
      <div class="doc-info">
        <h3>{{ doc.fileName }}</h3>
        <p>Автор: {{ doc.author }}</p>
        <p>Време на обработка: {{ (doc.processingTimeMs / 1000).toFixed(2) }} s</p>
        <span :class="['model-badge', doc.ocrModel]">
      {{ doc.ocrModel === 'tesseract' ? '⚡ Tesseract' : '🌐 Google Vision' }}
    </span>
        <p>Страници: {{ doc.totalPages }}</p>
      </div>
      <div class="button-container">
        <RouterLink :to="`/document/${doc.id}`" class="show-btn">
          Прикажи содржина
        </RouterLink>
        <button @click="deleteDocument(doc.id)" class="delete-btn">
          🗑 Избриши
        </button>
      </div>
    </div>



    </div>
<!--  </div>-->


</template>
<script setup>
import { ref, onMounted } from "vue";
import {RouterLink, useRouter} from "vue-router";

const documents = ref([]);


const router = useRouter();


const searchAuthor = ref("");
const searchTitle = ref("");
const filterModel = ref("");

let timeoutId = null;
const goBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push('/') ;
  }
}

const fetchDocuments = async () => {
  try {
    const res = await fetch("/api/documents");
    documents.value = await res.json();
  } catch (err) {
    console.error("Грешка при вчитување документи:", err);
  }
};


onMounted(fetchDocuments);


const goToContent = (docId) => {
  router.push(`/document/${docId}`);
};
const liveSearch = () => {
  clearTimeout(timeoutId);
  timeoutId = setTimeout(async () => {
    try {
      let url = "/api/documents";

      const params = new URLSearchParams();
      if (searchAuthor.value) params.append("author", searchAuthor.value);
      if (searchTitle.value) params.append("title", searchTitle.value);
      if (filterModel.value) params.append("model", filterModel.value);

      if ([...params].length > 0) {
        url = `/api/documents/search?${params.toString()}`;
      }

      const res = await fetch(url);
      documents.value = await res.json();
    } catch (err) {
      console.error("Грешка при пребарување документи:", err);
    }
  }, 300);
};
const deleteDocument = async (id) => {
  if (!confirm('Дали сте сигурни дека сакате да го избришете овој документ?')) return;

  try {
    await fetch(`/api/documents/${id}`, {
      method: 'DELETE'
    });
    documents.value = documents.value.filter(doc => doc.id !== id);
  } catch (err) {
    console.error('Грешка при бришење на документот:', err);
  }
};

//
// const liveSearch = () => {
//   clearTimeout(timeoutId);
//   timeoutId = setTimeout(async () => {
//     try {
//       let url = "http://localhost:8080/api/documents";
//
//       if (searchAuthor.value || searchTitle.value) {
//         const params = new URLSearchParams();
//         if (searchAuthor.value) params.append("author", searchAuthor.value);
//         if (searchTitle.value) params.append("title", searchTitle.value);
//         url = `http://localhost:8080/api/documents/search?${params.toString()}`;
//       }
//
//       const res = await fetch(url);
//       documents.value = await res.json();
//     } catch (err) {
//       console.error("Грешка при пребарување документи:", err);
//     }
//   }, 300);
// };
</script>

<style scoped>
.documents-container {
  max-width: 800px;
  margin: 60px auto;
  text-align: center;
  position: relative;
}

.doc-card {
  margin-top: 20px;
  padding: 20px;
  border-radius: 12px;
  background: #f3f4f6;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  color: black;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.doc-card:hover {
  transform: scale(1.02);
  transition: transform 0.2s;
}

.show-btn {
  margin-top: 15px;
  padding: 8px 16px;
  border-radius: 8px;
  background: #e0f2f0;
  color: #44A194;
  border: 1px solid #44A194;
  font-weight: 500;
  cursor: pointer;
  transition: 0.3s;
  text-decoration: none;
}

.show-btn:hover {
  background: #44A194;
  color: white;
  transform: translateY(-2px);
}
.nav-link {
  display: inline-block;
  padding: 10px 18px;
  border-radius: 10px;
  background: #FAD0CF;
  color: #1a1a1a;
  text-decoration: none;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(222, 100, 98,0.1);
  transition: 0.3s;
  margin: 10px;
}

.nav-link:hover {
  background: #EC8F8D;
  transform: translateY(-1px);
  color: #111827;
}

.search-container {
  display: flex;
  gap: 10px;
  z-index: 1000;
}


.search-container input {
  padding: 10px 16px;
  border-radius: 8px;
  background-color: #f5f3ff;
  color: #1a1a1a;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease, box-shadow 0.3s ease, background-color 0.3s ease, color 0.3s ease;
  border: none;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  margin-top: 5px;
}


.search-container input:focus {
  border-color: #FAD0CF;;
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.12);
  color: #1a1a1a;
}


.search-container button {
  padding: 8px 14px;
  border-radius: 8px;
  background: #FAD0CF;
  color: #1a1a1a;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.search-container button:hover {
  background-color:#EC8F8D;;
  transform: translateY(-2px);
}
.documents-container {
  max-width: 1000px;
  margin: 60px auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  text-align: center;
  position: relative;
}
.doc-card {
  padding: 20px;
  border-radius: 12px;
  background: #f3f4f6;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  color: black;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  transition: transform 0.2s;
}

.doc-card:hover {
  transform: scale(1.02);
}


.nav-link-2 {
  display: inline-block;
  padding: 10px 18px;
  border-radius: 10px;
  background: #FAD0CF;
  color: #1a1a1a;
  text-decoration: none;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(222, 100, 98,0.1);
  transition: 0.3s;
  margin: 10px;
}

.nav-link-2:hover {
  background: #EC8F8D;
  transform: translateY(-1px);
  color: #111827;
}

h1{
  color: #2c2c2c;;
}
.message-empty {
  color: #2c2c2c;
  padding: 20px 30px;
  border-radius: 12px;
  text-align: center;
  font-size: 1.2rem;
  font-weight: 500;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  margin: 2rem auto;
  max-width: 400px;
}
.doc-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;
  height: 300px;
  padding: 10px;
  overflow-y: auto;
}
.button-container {
  width: 100%;
  display: flex;
  justify-content: left;
  margin-top: 10px;
}
h1,h2{
  color: #44A194;
}
.header {
  width: 1250px;
  padding: 2px 2px;
  height: 100px;
  margin-top: 20px;
}
.top-nav img {
  height: 64px;
  width: auto;
  object-fit: contain;
}
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.top-nav {
  display: flex;
  align-items: center;
}

.nav-left,
.nav-center,
.nav-right {
  flex: 1;
  display: flex;
  align-items: center;
}

.nav-left {
  justify-content: flex-start;
}

.nav-center {
  justify-content: center;
}

.nav-right {
  justify-content: flex-end;
}


.logo {
  height: 64px;
}

.search-container {
  display: flex;
  gap: 12px;
}
.model-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}
.delete-btn {
  margin-top: 15px;
  margin-left: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  background: #fde8e8;
  color: #c0392b;
  border: 1px solid #e57373;
  font-weight: 500;
  cursor: pointer;
  transition: 0.3s;
}

.delete-btn:hover {
  background: #e74c3c;
  color: white;
  transform: translateY(-2px);
}
.model-badge.tesseract {
  background-color: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #43a047;
}

.model-badge.googlevision {
  background-color: #e3f2fd;
  color: #1565c0;
  border: 1px solid #1e88e5;
}

.model-filter {
  padding: 10px 16px;
  border-radius: 8px;
  background-color: #f5f3ff;
  color: #1a1a1a;
  font-size: 14px;
  outline: none;
  border: none;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  margin-top: 5px;
  cursor: pointer;
}
.nav-center select{
  margin-left: 5px;
}
h1, h2 {
  color: #44A194;
  font-size: 2.4rem;
  font-weight: 600;
  letter-spacing: 0.02em;
}

</style>