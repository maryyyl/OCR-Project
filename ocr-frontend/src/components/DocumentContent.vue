<template>
  <header>
    <div class="header">
      <div class="top-nav">
        <div class="nav-left">
          <div class="nav-buttons">
            <button class="nav-link" @click="goBack">
              Назад
            </button>
              <RouterLink class="nav-link" to="/">
                Додај нова книга
              </RouterLink>
          </div>
        </div>

        <div class="nav-right">
          <a target="_blank" href="https://www.finki.ukim.mk/mk/content/%D1%84%D0%B8%D0%BD%D0%BA%D0%B8-%D1%81%D0%BE-%D0%BF%D0%BE%D0%B4%D0%B4%D1%80%D1%88%D0%BA%D0%B0-%D0%BD%D0%B0-%D0%BC%D0%B4%D1%82-%D0%B8-%D0%B2%D0%BB%D0%B0%D0%B4%D0%B0%D1%82%D0%B0-%D0%B4%D0%BE%D0%B1%D0%B8-%D0%B5%D0%B2%D1%80%D0%BE%D0%BF%D1%81%D0%BA%D0%BE-%D1%84%D0%B8%D0%BD%D0%B0%D0%BD%D1%81%D0%B8%D1%80%D0%B0%D1%9A%D0%B5-%D0%B7%D0%B0-%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%BE%D1%82-%D0%B2%D0%B5%D0%B7%D0%B8%D0%BB%D0%BA%D0%B0-%D0%B7%D0%B0">
            <img src="/aifa_-_vezilka_-_tranparent%20(1).png" class="logo" />
          </a>

        </div>

      </div>
    </div>
  </header>
  <div class="content-page">
    <h1>{{ document?.fileName }}</h1>
    <p class="author-text">Автор: {{ document?.author }}</p>
    <div class="extracted-text">
      <p>{{ document?.extractedText }}</p>
    </div>

    <div class="download-buttons" v-if="document && document.extractedText">
      <button @click="downloadTxt" class="download-btn">⬇ Превземи како .txt</button>
      <button @click="downloadDoc" class="download-btn">⬇ Превземи како .doc</button>
    </div>


  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, RouterLink,useRouter } from "vue-router";


const route = useRoute();
const router = useRouter()
const document = ref(null);
const goBack = () => {
  router.back();
};

onMounted(async () => {
  const docId = route.params.id;
  const res = await fetch(`/api/documents/${docId}/text`);
  const data = await res.json();
  document.value = data;
});
const downloadTxt = () => {
  if (!document.value || !document.value.extractedText) return;

  const blob = new Blob([document.value.extractedText], { type: "text/plain" });
  const a = window.document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = document.value.fileName.replace(/\.[^/.]+$/, "") + ".txt";
  window.document.body.appendChild(a);
  a.click();
  window.document.body.removeChild(a);
};

const downloadDoc = () => {
  if (!document.value || !document.value.extractedText) return;

  const blob = new Blob([document.value.extractedText], { type: "application/msword" });
  const a = window.document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = document.value.fileName.replace(/\.[^/.]+$/, "") + ".doc";
  window.document.body.appendChild(a);
  a.click();
  window.document.body.removeChild(a);
};
</script>

<style scoped>
.content-page {
  max-width: 900px;
  margin: 40px auto;
  padding: 40px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.08);
  color: #111827;
}

.extracted-text {
  margin-top: 24px;
  padding: 24px;
  background: #f9fafb;
  border-radius: 10px;
  border-left: 4px solid #44A194;
  white-space: pre-wrap;
  text-align: left;
  font-size: 0.95rem;
  line-height: 1.7;
  color: #374151;
}

.nav-link {
  display: inline-block;
  padding: 10px 18px;
  border-radius: 10px;
  background: #FAD0CF;
  color: black;
  text-decoration: none;
  font-weight: 500;
  transition: 0.3s;
  margin-left: 8px;
}

.nav-link:hover {
  background: #EC8F8D;
  transform: translateY(-1px);
  color: #111827;
}
.author-text {
  display: inline-block;
  background: #e0f2f0;
  color: #44A194;
  padding: 4px 12px;
  border-radius: 7px;
  font-weight: 600;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.download-buttons {
  margin-top: 25px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}

.download-btn {
  padding: 10px 20px;
  border-radius: 8px;
  background: #e0f2f0;
  color: #44A194;
  border: 1px solid #44A194;
  font-weight: 500;
  cursor: pointer;
  transition: 0.3s;
}

.download-btn:hover {
  background: #44A194;
  color: white;
  transform: translateY(-2px);
}
h1 {
  color: #44A194;
  font-size: 2.4rem;
  font-weight: 300;
  letter-spacing: 0.05em;
  margin-bottom: 6px;
}
.header{
  height: 100px;
  width: 1200px;

}
.header {
  width: 1250px;
  padding: 2px 2px;
}

.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
}


.top-nav img {
  height: 64px;
  width: auto;
  object-fit: contain;
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

.nav-right {
  justify-content: flex-end;
}

.logo {
  height: 64px;
}


.nav-center select{
  margin-left: 5px;
}

</style>