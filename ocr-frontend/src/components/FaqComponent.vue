<template>
  <header>
    <div class="header">
      <div class="top-nav">
        <div class="nav-left">
          <div class="nav-buttons">
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
  <section class="faq-section">
    <div class="faq-header">
      <h2>Имаш прашање?</h2>
      <p>Овде најди одговор на твоите прашања 👇</p>
    </div>

    <div class="faq-list">
      <div
          v-for="(item, index) in faqItems"
          :key="index"
          class="faq-item"
          :class="{ 'is-open': openIndex === index }"
          @click="toggle(index)"
      >
        <div class="faq-question">
          <span>{{ item.question }}</span>
          <div class="faq-icon" :class="{ rotated: openIndex === index }">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <polyline points="6 9 12 15 18 9" />
            </svg>
          </div>
        </div>

        <Transition name="slide">
          <div v-if="openIndex === index" class="faq-answer">
            <p>{{ item.answer }}</p>
          </div>
        </Transition>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import {RouterLink} from "vue-router";

const openIndex = ref(null)

const toggle = (index) => {
  openIndex.value = openIndex.value === index ? null : index
}

const faqItems = ref([
  {
    question: 'Како да започнам со користење на услугата?',
    answer: 'Едноставно! Платформата е целосно бесплатна, јавна и отворена за користење — без регистрација, без најава, без никакви услови. Создадена е со една цел: да ги направи дигитализираните книги на македонски јазик достапни за секого, насекаде и во секое време.'
  },
  {
    question: 'Дали постои начин да изберам модел за обработка на документ?',
    answer: 'Да! Платформата ти дава можност сам да одлучиш кој модел најдобро одговара за твојот документ. Доколку документот е целосно на македонски јазик, Tesseract е одличен избор — лесен, брз и работи дури и без интернет. Од друга страна, ако документот содржи мешани јазици, сложен распоред на содржина или ракописен текст, Google Vision ќе даде значително подобри резултати, благодарение на моќната облак обработка која се справува и со документи од послаб квалитет.'
  },{
    question:'Кои формати на документи се поддржани?',
    answer:'Во моментот платформата поддржува PDF фајлови. За да го добиеш најдобриот можен резултат при обработката, препорачуваме да користиш скенирани документи со резолуција од минимум 300 DPI — колку поостра сликата, толку попрецизна обработката.'
  },
  {
    question:'Зошто резултатот од обработката содржи грешки?',
    answer:  'Грешките најчесто се резултат на слаб квалитет на скенот, нечитлив ракопис или мешање на јазици во документот. За подобри резултати препорачуваме документи со резолуција од минимум 300 DPI и да го избереш соодветниот модел — Tesseract за чисти македонски документи, Google Vision за посложени случаи.'
  },{
    question: 'Колку долго трае обработката?',
    answer:   'Обработката обично трае помеѓу 5 и 30 секунди, во зависност од големината на документот и избраниот модел. Tesseract е побрз за помали фајлови, додека Google Vision може да обработи посложени документи со повеќе страници.'
  }
])
</script>

<style scoped>
.faq-section {
  max-width: 720px;
  margin: 0 auto;
  padding: 60px 24px;
  font-family: 'Inter', sans-serif;
}

.faq-header {
  text-align: center;
  margin-bottom: 48px;
}

.faq-header h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #5dbaae;
  margin-bottom: 8px;
}

.faq-header p {
  color: #6b7280;
  font-size: 1rem;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.faq-item {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 24px;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
  text-align: left;
}

.faq-item:hover {
  box-shadow: 0 0 0 0 rgba(99, 102, 241, 0.08);
}

.faq-item.is-open {
  box-shadow: 0 0 0 0 rgba(99, 102, 241, 0.1);
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  font-weight: 600;
  font-size: 1rem;
  color: #111827;
  user-select: none;
}

.faq-icon {
  flex-shrink: 0;
  color: #6366f1;
  transition: transform 0.3s ease;
}

.faq-icon.rotated {
  transform: rotate(180deg);
}

.faq-answer {
  overflow: hidden;
}

.faq-answer p {
  margin-top: 14px;
  color: #4b5563;
  line-height: 1.7;
  font-size: 0.95rem;
}

/* Transition */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
  max-height: 200px;
}

.slide-enter-from,
.slide-leave-to {
  max-height: 0;
  opacity: 0;
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
.nav-center
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
}

.nav-link:hover {
  background: #EC8F8D;
  transform: translateY(-2px);
  color: #111827;
}
svg{
  color: #5dbaae;
}
h2{
  color: #5dbaae;
}
</style>