
<template>
  <header>
    <div class="header">

          <div class="top-nav">

            <div class="nav-left">
              <RouterLink to="/documents" class="nav-link">
                Погледни ги сите скенирани книги
              </RouterLink>
              <RouterLink to="/faq" class="nav-link">
                <span style="font-size: 1rem;">💬</span> Помош
              </RouterLink>
            </div>

            <div class="nav-right">
              <a target="_blank" href="https://www.finki.ukim.mk/mk/content/%D1%84%D0%B8%D0%BD%D0%BA%D0%B8-%D1%81%D0%BE-%D0%BF%D0%BE%D0%B4%D0%B4%D1%80%D1%88%D0%BA%D0%B0-%D0%BD%D0%B0-%D0%BC%D0%B4%D1%82-%D0%B8-%D0%B2%D0%BB%D0%B0%D0%B4%D0%B0%D1%82%D0%B0-%D0%B4%D0%BE%D0%B1%D0%B8-%D0%B5%D0%B2%D1%80%D0%BE%D0%BF%D1%81%D0%BA%D0%BE-%D1%84%D0%B8%D0%BD%D0%B0%D0%BD%D1%81%D0%B8%D1%80%D0%B0%D1%9A%D0%B5-%D0%B7%D0%B0-%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%BE%D1%82-%D0%B2%D0%B5%D0%B7%D0%B8%D0%BB%D0%BA%D0%B0-%D0%B7%D0%B0/">
                <img src="/aifa_-_vezilka_-_tranparent%20(1).png">
              </a>
            </div>

          </div>
    </div>
  </header>


  <div class="upload-container">
    <h1> Прикачете книга за OCR обработка</h1>

    <div class="title-input">
      <input
          type="text"
          id="title"
          v-model="title"
          placeholder="Внесете наслов на книгата"
          :disabled="documentData!==null && documentData.status==='PROCESSING'"
      />
    </div>

    <div class="author-input">
      <input
          type="text"
          id="author"
          v-model="author"
          placeholder="Внесете име на авторот"
          :disabled="documentData!==null && documentData.status==='PROCESSING'"
      />
    </div>

    <div class="model-select">
    <label for="ocrModel">Изберете модел за обработка:</label>
    <select
        id="ocrModel"
        v-model="selectedModel"
        :disabled="documentData!==null && documentData.status==='PROCESSING'"
        required
    >
      <option value="tesseract">Tesseract</option>
      <option value="googlevision">Google Vision</option>
    </select>

    <div class="model-info" :class="selectedModel">
    <span v-if="selectedModel === 'tesseract'">
      ⚡ <strong>Tesseract</strong> — Брз, работи <strong>offline</strong>, бесплатен. Препорачан за едноставни документи со јасен текст.
    </span>
      <span v-if="selectedModel === 'googlevision'">
        🌐 <strong>Google Vision</strong> — Попрецизен, работи <strong>online</strong>. Препорачан за сложени документи, ракопис или слаб квалитет на скенирање.
    </span>
    </div>
  </div>

    <label class="upload-box">
      <input :disabled="documentData!==null && documentData.status==='PROCESSING'" type="file" @change="handleFile" accept=".pdf" hidden />
      <div v-if="!file">
        <div class="icon">📄</div>
        <p>Кликнете или повлечете PDF документ</p>
        <span>Поддржан формат: <strong>.pdf</strong></span> <br>
        <span>Поддржан јазик: <strong>македонски</strong> </span>
      </div>

      <div v-else class="selected-file">
        ✅ Избран документ:
        <strong>{{ file.name }}</strong>
        <br>
         </div>
    </label>

    <button class="upload-btn" @click="uploadFile" :disabled="!file || !author || !title || uploading || (documentData !== null && documentData.status === 'PROCESSING')">
      {{ uploading ? "⏳ Се обработува..." : "🚀 Испрати документ" }}
    </button>

    <div v-if="uploading || progress > 0" class="progress-wrapper">
      <div class="progress-bar" :style="{ width: progress + '%' }"></div>
      <span>{{ progress }}%</span>
    </div>

    <div class="document-exists-box" v-if="message">
      {{ message }}
      <div class="link-to-document">
        <RouterLink v-if="link" :to="link" class="document-link">
          Прегледај го документот
        </RouterLink>
      </div>

    </div>

    <div v-if="documentData && documentData.status === 'DONE'" class="result-card">
      <h3 style="color: darkslategray">Извлечен текст</h3>
      <textarea rows="15" v-model="documentData.extractedText"></textarea>

      <div class="result-actions">
        <button class="save-document" @click="saveDocumentData" :disabled="saving">
          {{ saving ? '⏳ Се зачувува...' : 'Зачувај го документот' }}
        </button>
        <button
            class="diff-btn"
            v-if="hasChanges"
            @click="showDiff = !showDiff"
        >
          {{ showDiff ? '✖ Затвори разлики' : '🔍 Прикажи разлики' }}
        </button>
      </div>

      <div v-if="showDiff" class="diff-container">
        <div class="diff-header">
          <span>📄 Оригинал</span>
          <span>✏️ Изменето</span>
        </div>
        <div class="diff-body">
          <div
              v-for="(line, i) in computeDiff(originalText, documentData.extractedText)"
              :key="i"
              class="diff-row"
              :class="{ changed: line.changed }"
          >
            <div class="diff-cell original">{{ line.original || '&nbsp;' }}</div>
            <div class="diff-cell edited">{{ line.edited || '&nbsp;' }}</div>
          </div>
        </div>
      </div>

      <div v-if="show" class="toast">
        {{ toastMessage }}
        <div><RouterLink :to="toastLink">Прегледај го документот</RouterLink></div>
      </div>
    </div>
<!--    <div v-if="documentData && documentData.status === 'DONE'" class="result-card"> -->
<!--      -->
<!--      <h3 style="color: darkslategray">Извлечен текст</h3>-->
<!--      <textarea rows="15" v-model="documentData.extractedText"></textarea>-->

<!--      <button class="save-document" @click="saveDocumentData" :disabled="saving">-->
<!--        {{ saving ? '⏳ Се зачувува...' : 'Зачувај го документот' }}-->
<!--      </button>-->
<!--      -->
<!--      <div v-if="show" class="toast">-->
<!--        {{ toastMessage }}-->
<!--        <div>-->
<!--          <RouterLink :to="toastLink">Прегледај документ</RouterLink>-->
<!--        </div>-->
<!--      </div>-->
    </div>
<!--    </div>-->
  <StatsPanel/>
</template>

<script setup>
import FaqComponent from "./FaqComponent.vue";
import { RouterLink } from "vue-router";
import { ref,computed } from "vue";
import axios from 'axios';
import StatsPanel from "./StatsPanel.vue";
const file = ref(null);
const author = ref("");
const uploading = ref(false);
const progress = ref(0);
const message = ref("");
const documentData = ref(null);
const link=ref(null)
const show = ref(false);
const toastMessage=ref("")
const title = ref("");
const selectedModel=ref("tesseract")
const toastLink=ref("")
const originalText = ref("");


const handleFile = (event) => {
  file.value = event.target.files[0];
  message.value = "";
  progress.value = 0;
};

const uploadFile = async () => {
  link.value=null;
  if (!file.value || !author.value) {
    message.value = "Ве молиме внесете автор и изберете документ!";
    return;
  }


  const formData = new FormData();
  formData.append("file", file.value);
  formData.append("author", author.value);
  formData.append("title",title.value);
  formData.append("model",selectedModel.value);
  uploading.value = true;
  progress.value = 0;
  message.value = "Испраќање документ...";

  try {
    const res = await fetch("/api/documents/upload", {
      method: "POST",
      body: formData,
    });

    if (res.status === 409) {
      const data = await res.json();
      message.value = data.message + ' ';
      link.value = data.link;
      return;
    }

    const docId = await res.text();
    message.value = "Документот е испратен! OCR процесирањето започна.";

    const checkProgress = async () => {
      const res = await fetch(`/api/documents/${docId}`);
      const data = await res.json();
      documentData.value = data;

      if (data.totalPages > 0) {
        progress.value = Math.floor((data.processedPages / data.totalPages) * 100);
      }

      if (data.status !== "DONE" && data.status !== "FAILED") {
        setTimeout(checkProgress, 500);
      } else if (data.status === "DONE") {
        message.value = "OCR процесирањето заврши!";
        originalText.value = data.extractedText;
      } else if (data.status === "FAILED") {
        message.value = "Грешка при OCR процесирање!";
      }
    };

    checkProgress();
  } catch (err) {
    console.error(err);
    message.value = "Грешка при испраќање!";
  } finally {
    uploading.value = false;
  }
};
const saving = ref(false);


async function saveDocumentData() {
  if (!documentData.value) return;

  saving.value = true;
  try {
    const response = await axios.put(`/api/documents/${documentData.value.id}`, {
      extractedText: documentData.value.extractedText
    });

     toastLink.value = response.data.link;
    showToast('Документот е успешно зачуван ✅');
    console.log('Линк:', toastLink.value);

  } catch (err) {
    showToast('Грешка при зачувување на документот ❌');
    console.error(err);
  } finally {
    saving.value = false;
  }

}
function showToast(msg) {
  toastMessage.value = msg;
  show.value = true;
  setTimeout(() => show.value = false, 6000);
}
const showDiff = ref(false);

const hasChanges = computed(() => {
  return originalText.value !== documentData.value?.extractedText;
});

function computeDiff(original, edited) {
  const originalLines = original.split("\n");
  const editedLines = edited.split("\n");
  const maxLen = Math.max(originalLines.length, editedLines.length);
  const result = [];
  for (let i = 0; i < maxLen; i++) {
    const o = originalLines[i] ?? "";
    const e = editedLines[i] ?? "";
    result.push({ original: o, edited: e, changed: o !== e });
  }
  return result;
}

</script>

<style scoped>

.upload-container {
  max-width: 650px;
  margin: 60px auto;
  padding: 30px;
  border-radius: 20px;
  background: #ffffff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  text-align: center;
  font-family: "Segoe UI", sans-serif;
}

h1 {
  margin-bottom: 30px;
  color: #1f2937;
}

.toast {
  position: fixed;
  bottom: 26px;
  right: 20px;
  background: white;
  color: darkslategray;
  padding: 12px 20px;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.2);
  font-weight: 600;
  z-index: 1000;
}

.upload-box {
  display: block;
  padding: 40px;
  border: 2px dashed #de6462;
  border-radius: 16px;
  background: #f9fafb;
  cursor: pointer;
  transition: 0.3s;
  margin-top: 10px;
}

.upload-box:hover {
  background: #eef2ff;
  transform: scale(1.02);
}

.icon {
  font-size: 40px;
  margin-bottom: 10px;
}
.save-document {
  background: #e8f5f3;
  color: #44A194;
  font-weight: 600;
  border: none;
  border-radius: 12px;
  padding: 10px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(68, 161, 148, 0.1);
  height: 50px;
}

.save-document:hover {
  background: #d0ebe7;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(68, 161, 148, 0.15);
}

.save-document:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.save-document:active {
  transform: translateY(0);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.save-document:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}
.upload-box p {
  font-weight: 500;
  color: #374151;
}

.upload-box span {
  font-size: 13px;
  color: #6b7280;
}

.selected-file {
  color: #78350f;
  font-size: 15px;
}

.upload-btn {
  margin-top: 20px;
  padding: 12px 24px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #44A194, #a8d8d2);
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
}

.upload-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.upload-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.progress-wrapper {
  margin-top: 25px;
  background: #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  height: 25px;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #a5b4fc, #86efac);
  transition: width 0.4s ease;
  border-radius: 8px;
}

.progress-wrapper span {
  position: absolute;
  width: 100%;
  text-align: center;
  line-height: 25px;
  font-size: 13px;
  font-weight: bold;
  color: #111827;
}


.result-card {
  margin-top: 30px;
  padding: 20px;
  background: #f3f4f6;
  border-radius: 15px;
  margin-bottom: 10px;
}

textarea {
  width: 100%;
  margin-top: 10px;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid #d1d5db;
  resize: vertical;
}

.top-nav {
  text-align: center;
  margin-top: 30px;
}

.document-exists-box {
  margin-top: 20px;
  padding: 15px 20px;
  background-color: #faf9f7;
  border: 1px solid #e5e0d8;
  border-radius: 12px;
  color: #3d3530;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: default;
  letter-spacing: 0.01em;
}

.document-exists-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}
#wrong-document-text:hover {
  color: #374151;
}
.author-input,
.title-input {
  width: 95%;
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
}

.author-input input,
.title-input input {
  width: 100%;
  padding: 12px 15px;
  border-radius: 10px;
  border: 1px solid #d1d5db;
  background-color: #ffffff;
  font-size: 16px;
  color: #111827;
  outline: none;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.author-input input:focus,
.title-input input:focus {
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}
.link-to-document {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px 0;
}


input:-webkit-autofill {
  -webkit-box-shadow: 0 0 0px 1000px #f3f4f6 inset;
  -webkit-text-fill-color: #111827;
  transition: background-color 5000s ease-in-out 0s;
}


h2 {
  text-align: center;
  margin-bottom: 30px;
}


h1,h2{
  color: #44A194 ;
}

.header {
  height: 100px;
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


.nav-link {
  display: inline-block;
  padding: 10px 18px;
  border-radius: 10px;
  background: #FAD0CF;
  color: #1a1a1a;
  text-decoration: none;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: 0.3s;
  top:30px;
  margin-left: 8px;
}

.nav-link:hover {
  background: #EC8F8D;
  transform: translateY(-2px);
  color: #111827;
}
.model-select {
  margin: 16px 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.model-select label {
  font-weight: 500;
  font-size: 14px;
  text-align: left;
}

.model-select select {
  padding: 10px 14px;
  border-radius: 6px;
  border: 1px solid #cbd5e1;
  background-color: #f8fafc;
  font-size: 14px;
  outline: none;
  transition: 0.3s;
}

.model-select select:focus {
  box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.2);
}
.document-exists-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

h1 {
  font-size: 2rem;
  font-weight: 600;
  letter-spacing: 0.02em;
  color: #44A194;
}
.model-info.tesseract {
  background: #e8f5e9;
  border-left: 3px solid #43a047;
  color: #2e7d32;
  padding: 8px 12px;
  border-radius: 6px;
  margin-top: 10px;
}
.model-info.googlevision {
  background: #e3f2fd;
  border-left: 3px solid #1e88e5;
  color: #1565c0;
  padding: 8px 12px;
  border-radius: 6px;
  margin-top: 10px;
}
.toast a {
  display: inline-block;
  margin-top: 8px;
  padding: 6px 14px;
  border-radius: 8px;
  background: #e0f2f0;
  color: #44A194;
  font-weight: 500;
  text-decoration: none;
  transition: 0.3s;
}

.toast a:hover {
  background: #44A194;
  color: white;
  transform: translateY(-2px);
}
.document-link {
  display: inline-block;
  padding: 10px 18px;
  background-color: #e8e0d5;
  color: #1a1a1a;
  font-weight: 600;
  border-radius: 8px;
  text-decoration: none;
  transition: background-color 0.2s, transform 0.15s;
}

.document-link:hover {
  background-color: #d6cdc0;
  transform: translateY(-1px);
}
.result-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 10px;
  flex-wrap: wrap;
}

.diff-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 12px;
  background: #e8f5f3;
  color: #44A194;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(68, 161, 148, 0.1);
}

.diff-btn:hover {
  background: #d0ebe7;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(68, 161, 148, 0.15);
}

.diff-container {
  margin-top: 20px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  text-align: left;
  font-size: 13px;
}

.diff-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  background: #f3f4f6;
  font-weight: 700;
  color: #374151;
  padding: 10px 16px;
  border-bottom: 1px solid #e5e7eb;
}

.diff-body {
  max-height: 400px;
  overflow-y: auto;
}

.diff-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  border-bottom: 1px solid #f3f4f6;
}

.diff-row.changed .diff-cell.original {
  background-color: #fee2e2;
  color: #991b1b;
}

.diff-row.changed .diff-cell.edited {
  background-color: #dcfce7;
  color: #166534;
}

.diff-cell {
  padding: 6px 12px;
  white-space: pre-wrap;
  word-break: break-word;
  border-right: 1px solid #e5e7eb;
}

</style>