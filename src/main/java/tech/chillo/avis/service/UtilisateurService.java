package tech.chillo.avis.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tech.chillo.avis.TypeDeRole;
import tech.chillo.avis.entite.Jwt;
import tech.chillo.avis.entite.Role;
import tech.chillo.avis.entite.Utilisateur;
import tech.chillo.avis.entite.Validation;
import tech.chillo.avis.repository.JwtRepository;
import tech.chillo.avis.repository.UtilisateurRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UtilisateurService implements UserDetailsService {
    private UtilisateurRepository utilisateurRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private ValidationService validationService;
    private JwtRepository jwtRepository;

    public void inscription(Utilisateur utilisateur) {

        if (!utilisateur.getEmail().contains("@")) {
            throw new RuntimeException("Votre mail invalide");
        }
        if (!utilisateur.getEmail().contains(".")) {
            throw new RuntimeException("Votre mail invalide");
        }

        final Optional<Utilisateur> utilisateurOptional = this.utilisateurRepository.findByEmail(utilisateur.getEmail());
        if (utilisateurOptional.isPresent()) {
            throw new RuntimeException("Votre mail est déjà utilisé");
        }
        final String mdpCrypte = this.passwordEncoder.encode(utilisateur.getMdp());
        utilisateur.setMdp(mdpCrypte);

        final Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeDeRole.UTILISATEUR);
        if (utilisateur.getRole() != null && utilisateur.getRole().getLibelle().equals(TypeDeRole.ADMINISTRATEUR)) {
            roleUtilisateur.setLibelle(TypeDeRole.ADMINISTRATEUR);
            utilisateur.setActif(true);
        }
        utilisateur.setRole(roleUtilisateur);

        utilisateur = this.utilisateurRepository.save(utilisateur);

        if (roleUtilisateur.getLibelle().equals(TypeDeRole.UTILISATEUR)) {
            this.validationService.enregistrer(utilisateur);
        }
    }

    public void activation(final Map<String, String> activation) {
        final Validation validation = this.validationService.lireEnFonctionDuCode(activation.get("code"));

        Instant dateTimeNow = Instant.now();
        if (dateTimeNow.isAfter(validation.getExpiration())) {
            throw new RuntimeException("Votre code a expiré");
        }

        final Utilisateur utilisateurActiver = this.utilisateurRepository.findById(validation.getUtilisateur().getId()).orElseThrow(() -> new RuntimeException("Utilisateur inconnu"));
        utilisateurActiver.setActif(true);
        this.utilisateurRepository.save(utilisateurActiver);

        validation.setActivation(dateTimeNow);
        this.validationService.update(validation);
    }

    @Override
    public Utilisateur loadUserByUsername(final String username) throws UsernameNotFoundException {
        return this.utilisateurRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur ne corespond à cet identifiant"));
    }

    public void deconnexion() {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Jwt jwt = this.jwtRepository
                .findUtilisateurValidToken(utilisateur.getEmail(), false, false)
                .orElseThrow(() -> new RuntimeException("username invalid"));

        jwt.setExpire(true);
        jwt.setDesactive(true);
        jwtRepository.save(jwt);
    }
}
