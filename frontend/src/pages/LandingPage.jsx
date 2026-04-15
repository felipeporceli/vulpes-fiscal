import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  Shield,
  Zap,
  CheckCircle,
  Sparkles,
  BarChart3,
  Lock,
  ChevronDown,
  Menu,
  X,
  Mail,
  Phone,
  MapPin,
  ArrowRight,
  Send,
  FileText,
} from 'lucide-react';

const fadeUp = {
  hidden: { opacity: 0, y: 30 },
  visible: (delay = 0) => ({
    opacity: 1,
    y: 0,
    transition: { duration: 0.6, delay, ease: 'easeOut' },
  }),
};

// ─── Navbar ───────────────────────────────────────────────────────────────────
function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const navItems = [
    { label: 'Sobre nós', href: '#sobre' },
    { label: 'Nossos valores', href: '#valores' },
    { label: 'Entre em contato', href: '#contato' },
  ];

  return (
    <nav
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
        scrolled
          ? 'bg-vulpes-dark/95 backdrop-blur-md shadow-lg shadow-black/30'
          : 'bg-transparent'
      }`}
    >
      <div className="container-max flex items-center justify-between py-3 px-6 md:px-12 lg:px-24">
        {/* Logo */}
        <a href="#" className="flex items-center gap-3 group">
          <img src="/vulpeslogo.png" alt="Vulpes Fiscal" className="h-10 w-auto" />
          <span className="text-white font-bold text-lg tracking-tight">
            Vulpes<span className="text-vulpes-orange">Fiscal</span>
          </span>
        </a>

        {/* Desktop nav */}
        <div className="hidden md:flex items-center gap-8">
          {navItems.map((item) => (
            <a
              key={item.href}
              href={item.href}
              className="text-slate-300 hover:text-white text-sm font-medium transition-colors duration-200"
            >
              {item.label}
            </a>
          ))}
          <Link
            to="/sistema"
            className="flex items-center gap-1.5 bg-vulpes-orange hover:bg-vulpes-orange-hover text-white text-sm font-semibold px-5 py-2.5 rounded-full transition-all duration-200 shadow-md shadow-vulpes-orange/30 hover:scale-105"
          >
            Acesse o emissor
            <ArrowRight size={14} />
          </Link>
        </div>

        {/* Mobile toggle */}
        <button
          className="md:hidden text-white p-2"
          onClick={() => setMobileOpen(!mobileOpen)}
          aria-label="Toggle menu"
        >
          {mobileOpen ? <X size={22} /> : <Menu size={22} />}
        </button>
      </div>

      {/* Mobile menu */}
      {mobileOpen && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="md:hidden bg-vulpes-dark/98 border-t border-white/10 px-6 py-4 flex flex-col gap-4"
        >
          {navItems.map((item) => (
            <a
              key={item.href}
              href={item.href}
              className="text-slate-300 hover:text-white font-medium py-1 transition-colors"
              onClick={() => setMobileOpen(false)}
            >
              {item.label}
            </a>
          ))}
          <Link
            to="/sistema"
            className="flex items-center justify-center gap-1.5 bg-vulpes-orange text-white font-semibold px-5 py-2.5 rounded-full mt-2"
            onClick={() => setMobileOpen(false)}
          >
            Acesse o emissor <ArrowRight size={14} />
          </Link>
        </motion.div>
      )}
    </nav>
  );
}

// ─── Hero ─────────────────────────────────────────────────────────────────────
function HeroSection() {
  return (
    <section
      id="hero"
      className="relative min-h-screen flex items-center overflow-hidden"
      style={{ background: 'linear-gradient(135deg, #032A47 0%, #08315B 55%, #032A47 100%)' }}
    >
      {/* Background grid */}
      <div
        className="absolute inset-0 opacity-[0.07]"
        style={{
          backgroundImage:
            'linear-gradient(rgba(254,96,12,0.5) 1px, transparent 1px), linear-gradient(90deg, rgba(254,96,12,0.5) 1px, transparent 1px)',
          backgroundSize: '60px 60px',
        }}
      />

      {/* Decorative glow blobs */}
      <div className="absolute top-1/3 left-1/4 w-96 h-96 bg-vulpes-orange/10 rounded-full blur-3xl animate-pulse-slow pointer-events-none" />
      <div className="absolute bottom-1/4 right-1/3 w-64 h-64 bg-vulpes-medium/40 rounded-full blur-3xl animate-pulse-slow pointer-events-none" />

      <div className="container-max section-padding relative z-10 w-full">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Left — text */}
          <div>
            {/* Badge */}
            <motion.div
              variants={fadeUp}
              initial="hidden"
              animate="visible"
              custom={0}
              className="inline-flex items-center gap-2 bg-white/10 border border-white/20 backdrop-blur-sm text-vulpes-orange text-xs font-semibold px-4 py-1.5 rounded-full mb-6 tracking-wider uppercase"
            >
              <span className="w-1.5 h-1.5 rounded-full bg-vulpes-orange animate-ping inline-block" />
              Plataforma de Emissão de NFC-e
            </motion.div>

            {/* Heading */}
            <motion.h1
              variants={fadeUp}
              initial="hidden"
              animate="visible"
              custom={0.1}
              className="text-4xl md:text-5xl lg:text-6xl font-extrabold text-white leading-tight mb-6"
            >
              Emita NFC-e com{' '}
              <span className="text-gradient">segurança</span>
              <br />e agilidade
            </motion.h1>

            {/* Subtitle */}
            <motion.p
              variants={fadeUp}
              initial="hidden"
              animate="visible"
              custom={0.2}
              className="text-slate-300 text-lg max-w-lg mb-10 leading-relaxed"
            >
              A solução completa para emissão de Nota Fiscal do Consumidor Eletrônica —
              simples, rápida e 100% em conformidade com a SEFAZ.
            </motion.p>

            {/* CTAs */}
            <motion.div
              variants={fadeUp}
              initial="hidden"
              animate="visible"
              custom={0.3}
              className="flex flex-col sm:flex-row items-start gap-4 mb-14"
            >
              <a
                href="#sobre"
                className="flex items-center gap-2 border-2 border-white/30 hover:border-vulpes-orange text-white hover:text-vulpes-orange font-semibold px-7 py-3.5 rounded-full transition-all duration-200"
              >
                Conheça mais
                <ChevronDown size={16} />
              </a>
              <Link
                to="/sistema"
                className="flex items-center gap-2 bg-vulpes-orange hover:bg-vulpes-orange-hover text-white font-semibold px-7 py-3.5 rounded-full shadow-lg shadow-vulpes-orange/30 transition-all duration-200 hover:scale-105"
              >
                Acesse o emissor
                <ArrowRight size={16} />
              </Link>
            </motion.div>

            {/* Floating stats */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5, duration: 0.7 }}
              className="flex flex-wrap gap-4"
            >
              {[
                { value: '99,9%', label: 'de disponibilidade' },
                { value: '< 3s', label: 'para emitir NFC-e' },
                { value: '100%', label: 'conforme SEFAZ' },
              ].map((stat) => (
                <div
                  key={stat.label}
                  className="glass rounded-2xl px-5 py-3 text-center"
                >
                  <p className="text-xl font-extrabold text-vulpes-orange">{stat.value}</p>
                  <p className="text-slate-400 text-xs mt-0.5">{stat.label}</p>
                </div>
              ))}
            </motion.div>
          </div>

          {/* Right — logo */}
          <motion.div
            initial={{ opacity: 0, x: 40 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.8, delay: 0.2 }}
            className="hidden lg:flex justify-center items-center"
          >
            <div className="relative">
              {/* Orange glow behind logo */}
              <div className="absolute inset-0 bg-vulpes-orange/20 rounded-full blur-3xl scale-75" />
              <img
                src="/vulpeslogo.png"
                alt="Vulpes Fiscal"
                className="relative h-80 xl:h-96 w-auto animate-float drop-shadow-2xl"
                style={{ filter: 'drop-shadow(0 0 40px rgba(254,96,12,0.25))' }}
              />
            </div>
          </motion.div>
        </div>
      </div>

      {/* Scroll indicator */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 1.2 }}
        className="absolute bottom-8 left-1/2 -translate-x-1/2 flex flex-col items-center gap-1 text-slate-400 text-xs"
      >
        <span>role para baixo</span>
        <ChevronDown size={16} className="animate-bounce" />
      </motion.div>
    </section>
  );
}

// ─── Sobre nós ────────────────────────────────────────────────────────────────
function AboutSection() {
  const features = [
    { icon: Zap,         label: 'Emissão em segundos',  desc: 'NFC-e geradas e transmitidas à SEFAZ em menos de 3 segundos.' },
    { icon: Lock,        label: 'Segurança garantida',   desc: 'Criptografia ponta a ponta e backups automáticos diários.' },
    { icon: BarChart3,   label: 'Relatórios completos',  desc: 'Acompanhe todas as emissões com dashboards em tempo real.' },
    { icon: CheckCircle, label: '100% SEFAZ',            desc: 'Homologado e em conformidade com a legislação vigente.' },
  ];

  return (
    <section id="sobre" className="bg-white section-padding">
      <div className="container-max">
        <div className="grid lg:grid-cols-2 gap-16 items-center">
          {/* Text */}
          <motion.div variants={fadeUp} initial="hidden" whileInView="visible" viewport={{ once: true }}>
            <span className="text-vulpes-orange text-sm font-semibold uppercase tracking-widest">
              Sobre nós
            </span>
            <h2 className="text-3xl md:text-4xl font-extrabold text-vulpes-dark mt-3 mb-5 leading-tight">
              Simplificamos a gestão fiscal do seu negócio
            </h2>
            <p className="text-slate-500 leading-relaxed mb-4">
              A Vulpes Fiscal nasceu com o objetivo de tornar a emissão de notas fiscais
              eletrônicas acessível para todos os tipos de negócios — do pequeno varejo ao
              grande estabelecimento.
            </p>
            <p className="text-slate-500 leading-relaxed">
              Com uma plataforma moderna e intuitiva, oferecemos todas as ferramentas
              necessárias para manter sua empresa em dia com as obrigações fiscais, sem
              complicações e sem perda de tempo.
            </p>
            <Link
              to="/sistema"
              className="inline-flex items-center gap-2 mt-8 bg-vulpes-orange hover:bg-vulpes-orange-hover text-white font-semibold px-7 py-3.5 rounded-full transition-all duration-200 shadow-md shadow-vulpes-orange/20"
            >
              Começar agora
              <ArrowRight size={16} />
            </Link>
          </motion.div>

          {/* Feature grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {features.map((f, i) => (
              <motion.div
                key={f.label}
                variants={fadeUp}
                initial="hidden"
                whileInView="visible"
                viewport={{ once: true }}
                custom={i * 0.1}
                className="group p-5 rounded-2xl border border-slate-100 hover:border-vulpes-orange/30 hover:shadow-lg hover:shadow-vulpes-orange/5 transition-all duration-300"
              >
                <div className="w-10 h-10 rounded-xl bg-vulpes-dark/5 group-hover:bg-vulpes-orange/10 flex items-center justify-center mb-3 transition-colors duration-300">
                  <f.icon size={20} className="text-vulpes-medium group-hover:text-vulpes-orange transition-colors duration-300" />
                </div>
                <h3 className="font-semibold text-vulpes-dark mb-1 text-sm">{f.label}</h3>
                <p className="text-slate-500 text-xs leading-relaxed">{f.desc}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}

// ─── Nossos valores ───────────────────────────────────────────────────────────
function ValuesSection() {
  const values = [
    { icon: Shield,      title: 'Segurança',    desc: 'Protegemos seus dados com os mais altos padrões de segurança, certificados digitais e conformidade com a LGPD.',  color: 'from-vulpes-dark to-vulpes-medium' },
    { icon: Zap,         title: 'Eficiência',   desc: 'Processos otimizados para que sua equipe ganhe tempo. Do cadastro à emissão, cada etapa foi pensada para ser ágil.', color: 'from-vulpes-orange to-orange-400' },
    { icon: CheckCircle, title: 'Conformidade', desc: 'Sempre em dia com a legislação e as normas da SEFAZ. Atualizações automáticas quando as regras fiscais mudam.',       color: 'from-emerald-500 to-green-600' },
    { icon: Sparkles,    title: 'Inovação',     desc: 'Tecnologia de ponta para uma gestão fiscal moderna. APIs abertas, integrações e uma interface sempre evoluindo.',      color: 'from-purple-500 to-pink-500' },
  ];

  return (
    <section id="valores" className="bg-slate-50 section-padding">
      <div className="container-max">
        <motion.div variants={fadeUp} initial="hidden" whileInView="visible" viewport={{ once: true }} className="text-center mb-14">
          <span className="text-vulpes-orange text-sm font-semibold uppercase tracking-widest">
            Nossos valores
          </span>
          <h2 className="text-3xl md:text-4xl font-extrabold text-vulpes-dark mt-3 mb-4">
            Os princípios que nos guiam
          </h2>
          <p className="text-slate-500 max-w-xl mx-auto">
            Cada decisão que tomamos é baseada nos valores que construímos desde o início.
          </p>
        </motion.div>

        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {values.map((v, i) => (
            <motion.div
              key={v.title}
              variants={fadeUp}
              initial="hidden"
              whileInView="visible"
              viewport={{ once: true }}
              custom={i * 0.12}
              className="group bg-white rounded-2xl p-6 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 border border-slate-100"
            >
              <div className={`w-12 h-12 rounded-2xl bg-gradient-to-br ${v.color} flex items-center justify-center mb-5 shadow-md`}>
                <v.icon size={22} className="text-white" />
              </div>
              <h3 className="text-vulpes-dark font-bold text-lg mb-2">{v.title}</h3>
              <p className="text-slate-500 text-sm leading-relaxed">{v.desc}</p>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}

// ─── Entre em contato ─────────────────────────────────────────────────────────
function ContactSection() {
  const [form, setForm] = useState({ nome: '', email: '', mensagem: '' });
  const [sent, setSent] = useState(false);

  const handleChange = (e) => setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: Integrar com API de contato
    setSent(true);
    setTimeout(() => setSent(false), 4000);
    setForm({ nome: '', email: '', mensagem: '' });
  };

  return (
    <section
      id="contato"
      className="section-padding"
      style={{ background: 'linear-gradient(135deg, #032A47 0%, #08315B 100%)' }}
    >
      <div className="container-max">
        <div className="grid lg:grid-cols-2 gap-14 items-start">
          {/* Info */}
          <motion.div variants={fadeUp} initial="hidden" whileInView="visible" viewport={{ once: true }}>
            <span className="text-vulpes-orange text-sm font-semibold uppercase tracking-widest">
              Entre em contato
            </span>
            <h2 className="text-3xl md:text-4xl font-extrabold text-white mt-3 mb-4">
              Fale com a nossa equipe
            </h2>
            <p className="text-slate-400 leading-relaxed mb-10">
              Tem dúvidas sobre a Vulpes Fiscal? Nossa equipe está pronta para ajudar.
              Descubra como podemos transformar a gestão fiscal do seu negócio.
            </p>

            <div className="space-y-5">
              {[
                { icon: Mail,   label: 'E-mail',      value: 'contato@vulpesfiscal.com.br' },
                { icon: Phone,  label: 'Telefone',    value: '(11) 9 0000-0000' },
                { icon: MapPin, label: 'Localização', value: 'São Paulo, SP — Brasil' },
              ].map((item) => (
                <div key={item.label} className="flex items-center gap-4">
                  <div className="w-10 h-10 rounded-xl bg-vulpes-orange/15 flex items-center justify-center flex-shrink-0">
                    <item.icon size={18} className="text-vulpes-orange" />
                  </div>
                  <div>
                    <p className="text-slate-400 text-xs">{item.label}</p>
                    <p className="text-white font-medium text-sm">{item.value}</p>
                  </div>
                </div>
              ))}
            </div>
          </motion.div>

          {/* Form */}
          <motion.div variants={fadeUp} initial="hidden" whileInView="visible" viewport={{ once: true }} custom={0.15}>
            <form
              onSubmit={handleSubmit}
              className="bg-white/5 backdrop-blur-sm border border-white/10 rounded-3xl p-8 space-y-5"
            >
              {['nome', 'email', 'mensagem'].map((field) => (
                <div key={field}>
                  <label className="block text-slate-300 text-xs font-medium mb-1.5 capitalize">
                    {field === 'nome' ? 'Nome completo' : field === 'email' ? 'E-mail' : 'Mensagem'}
                  </label>
                  {field === 'mensagem' ? (
                    <textarea
                      name={field}
                      value={form[field]}
                      onChange={handleChange}
                      required
                      rows={4}
                      placeholder="Como podemos ajudar?"
                      className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-vulpes-orange focus:ring-1 focus:ring-vulpes-orange transition-colors resize-none"
                    />
                  ) : (
                    <input
                      type={field === 'email' ? 'email' : 'text'}
                      name={field}
                      value={form[field]}
                      onChange={handleChange}
                      required
                      placeholder={field === 'email' ? 'seu@email.com.br' : 'Seu nome'}
                      className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-vulpes-orange focus:ring-1 focus:ring-vulpes-orange transition-colors"
                    />
                  )}
                </div>
              ))}
              <button
                type="submit"
                className="w-full flex items-center justify-center gap-2 bg-vulpes-orange hover:bg-vulpes-orange-hover text-white font-semibold py-3.5 rounded-xl transition-all duration-200 shadow-lg shadow-vulpes-orange/20 hover:scale-[1.02]"
              >
                {sent ? <><CheckCircle size={16} /> Mensagem enviada!</> : <><Send size={16} /> Enviar mensagem</>}
              </button>
            </form>
          </motion.div>
        </div>
      </div>
    </section>
  );
}

// ─── Footer ───────────────────────────────────────────────────────────────────
function Footer() {
  return (
    <footer className="bg-vulpes-dark border-t border-white/5 py-10 px-6 md:px-12 lg:px-24">
      <div className="container-max flex flex-col md:flex-row items-center justify-between gap-6">
        <div className="flex items-center gap-3">
          <img src="/vulpeslogo.png" alt="Vulpes Fiscal" className="h-8 w-auto" />
          <span className="text-white font-bold">
            Vulpes<span className="text-vulpes-orange">Fiscal</span>
          </span>
        </div>
        <div className="flex flex-wrap justify-center gap-6 text-slate-500 text-sm">
          <a href="#sobre"   className="hover:text-white transition-colors">Sobre nós</a>
          <a href="#valores" className="hover:text-white transition-colors">Nossos valores</a>
          <a href="#contato" className="hover:text-white transition-colors">Contato</a>
          <Link to="/sistema" className="hover:text-vulpes-orange transition-colors">Emissor</Link>
        </div>
        <p className="text-slate-600 text-xs text-center">
          © {new Date().getFullYear()} Vulpes Fiscal. Todos os direitos reservados.
        </p>
      </div>
    </footer>
  );
}

// ─── Page ─────────────────────────────────────────────────────────────────────
export default function LandingPage() {
  return (
    <div className="min-h-screen">
      <Navbar />
      <HeroSection />
      <AboutSection />
      <ValuesSection />
      <ContactSection />
      <Footer />
    </div>
  );
}
